# Dovewing Car Rentals — Fleet and Rental Tracker (rebuilt)

Dovewing's front desk uses this program to track its fleet and rentals. The
original worked, but staff kept a paper backup because the same car could show
as rented to two people at once. This repository is a rebuild of that program:
the same features and the same results, reorganised so the rules cannot be
broken from outside and a new vehicle class can be added without a rewrite.

- **Diagnosis and design sketch:** [DIAGNOSIS.md](DIAGNOSIS.md) (committed before
  any code change)
- **Original output:** [docs/original-output.txt](docs/original-output.txt)
- **Rebuilt output:** [docs/rebuilt-output.txt](docs/rebuilt-output.txt)

## Running it

The program is plain Java with no external dependencies. You need **JDK 21**.

**With Maven** (from the project root):

```bash
mvn -q compile exec:java
```

**Or directly with the JDK** (from the project root, in Git Bash):

```bash
javac -d target/classes $(find src/main/java -name "*.java")
java -cp target/classes com.dovewing.app.RentalTrackerApp
```

## Project structure

```text
src/main/java/com/dovewing/
├── domain/                     business rules; never prints
│   ├── Depot.java              the only way to rent or return a vehicle
│   ├── Vehicle.java            abstract; owns the vehicle status rule
│   ├── EconomyVehicle.java     economy deposit rule
│   ├── PremiumVehicle.java     premium deposit rule
│   ├── Renter.java             a customer: name and type
│   ├── RenterType.java         CASUAL / MEMBER; knows whether the deposit is waived
│   ├── Rental.java             one rental: its vehicle, renter, days, cost, deposit
│   ├── RentalStatus.java       ACTIVE / RETURNED
│   ├── VehicleStatus.java      AVAILABLE / RENTED
│   └── VehicleStateException.java
└── app/                        presentation only
    ├── RentalTrackerApp.java   runs the day; prints refusals
    └── ReportPrinter.java      formats every line of output
```

## Behaviour preserved

The rebuild keeps every original feature and result: renting, returning, cost
(daily rate × days), deposit (the vehicle's deposit, waived for members), and the
end-of-day report, with every line in the same format.

There is exactly **one intended change in behaviour**, which the brief requires:
a vehicle can never be available and rented at the same time. The improvement
(see below) **adds** one line at the end of the report and changes none.

Comparing the original output with the rebuilt output:

```bash
git diff --no-index docs/original-output.txt docs/rebuilt-output.txt
```

```diff
 Returned Toyota Prado (GR-5678-22)
-Rented Toyota Vitz (GT-1234-20) to Ama Owusu for 2 days, cost GHS 300.0, deposit GHS 0.0
+Refused: Toyota Vitz (GT-1234-20) is already rented
 ...
 Active rentals:
   Rental #1: GT-1234-20 -> Kofi Mensah (3 days)
-  Rental #3: GT-1234-20 -> Ama Owusu (2 days)
+Deposits held: GHS 500.0
```

| Difference | Why |
|---|---|
| The second Vitz rental is refused | The Vitz is already out with Kofi. This is the rule the business says must never be broken. |
| Rental #3 no longer appears | The refused rental is never created, so the report shows one active rental for the Vitz instead of two. No rental number is used up by a refusal. |
| `Deposits held` line added | The improvement. It is appended; no original line changes. |

Every other line is identical. The first three lines, the fleet status (the
Vitz still `RENTED`, the Prado `AVAILABLE`) and Kofi's Rental #1 all match the
original exactly.

**How this was checked:** after every refactoring commit, the program's output
was compared with `docs/original-output.txt` and had to be identical. The one
intended change was made in its own commit (`fix: refuse renting a vehicle that
is already rented`), which also saved `docs/rebuilt-output.txt`. Every later
commit was checked against that file. The commit history shows each step.

### Protection: what outside code can no longer do

Each of these was tried from outside the domain package and is now refused:

| Attempt | Result |
|---|---|
| Set a vehicle's status directly | Does not compile: the field is private |
| Call `markRented()` / `markReturned()` from outside the domain | Does not compile: package-private |
| Create a `Rental` without going through the depot | Does not compile: package-private constructor |
| `new Vehicle(...)` | Does not compile: `Vehicle` is abstract |
| Clear or edit `getRentals()`, `getVehicles()`, `getActiveRentals()`, `rentalsFor()` | `UnsupportedOperationException`: read-only copies |
| Rent a vehicle that is already out | `VehicleStateException` |
| Return a vehicle that is not out | `VehicleStateException` |
| Rent for 0 or negative days, or a vehicle or renter not in this depot | `IllegalArgumentException`, before anything changes |
| Add the same vehicle twice, or a second vehicle with the same registration | `IllegalArgumentException` |
| Blank names or registrations, a non-positive rate, a negative deposit, `null` arguments | Refused when the object is created or the method is called |

## Design notes

### The rule at the centre: a vehicle is available or rented, never both

In the original, whether a car was out was stored in two places (`Vehicle.status`
and each `Rental.status`) and nothing kept them in step. Now:

- `Vehicle` **owns** its status. Its only state changes are the package-private
  `markRented()` and `markReturned()`, and each refuses an invalid change by
  throwing `VehicleStateException`. Because the guard sits where the state lives,
  no caller, now or later, can rent a car twice.
- `Depot` is the only class that calls them, and it changes the vehicle and its
  rental **together, in one method**: `rent()` marks the vehicle rented and
  records the rental; `returnVehicle()` finds the one active rental for that
  vehicle and closes both.
- Each method **checks first, then changes, then records.** In `rent()`,
  `markRented()` is both the last check and the first change, so a refused rental
  leaves no trace: no rental, no record, no rental number used.

**Alternative considered:** storing no status on `Vehicle` at all and deriving it
from "is there an active rental for this car?". That also removes the second
source of truth, but every status question would have to go through `Depot`, and
the rule would no longer live with the object it protects. I kept a stored status
guarded by `Vehicle`, which is simpler to follow and impossible to bypass because
only the domain package can change it.

### Vehicle classes: an abstract class with subclasses

`Vehicle` is abstract and holds what every vehicle shares: model, registration,
daily rate, deposit and status. `EconomyVehicle` and `PremiumVehicle` extend it,
and each implements `depositFor(Renter)` and `getCategory()`. `Depot.rent()` just
asks the vehicle `costFor(days)` and `depositFor(renter)`; Java runs the right
version for the actual vehicle, so `rent()` no longer branches on category.

- The **cost rule** (`costFor`) is `final` in `Vehicle`: it is the same for every
  vehicle, and no subclass can change it.
- The **member waiver** lives once, in `standardDeposit()`, a `protected final`
  helper. A subclass can use it but cannot alter it.
- `depositFor()` is **abstract**, so every vehicle class must decide its deposit
  rule or it will not compile. Today Economy and Premium both decide "standard";
  the van will decide something else.
- The subclasses are `final`, so a van cannot be built by extending Premium to
  reuse its code. A van is not a premium car.

**Alternatives considered:**

- **An enum plus a `switch` in `rent()`.** The compiler checks every case, but each
  new vehicle class still means editing the central rental method, which is the
  problem the brief describes ("a tweak to premium pricing once broke how economy
  returns were handled").
- **A separate deposit-rule object that each vehicle has (composition).** This is
  also defensible and would suit rules that change at runtime or vary
  independently of the vehicle class. Here the deposit rule belongs to the vehicle
  class itself, and an abstract class also gives one place for the shared state
  and the status rule, so inheritance is the simpler fit.

### Renter type: an enum that carries its rule

`RenterType` replaces the `"CASUAL"` / `"MEMBER"` strings. Each constant carries
whether its deposit is waived (`waivesDeposit()`), so a mistyped renter type is a
compile error instead of a member silently charged a deposit, and the waiver rule
sits with the thing it describes.

### Rental: a class, not a record

A record suits an immutable value. A rental's status changes when the car comes
back, so `Rental` is a class with final fields for everything that never changes
and one guarded status. It holds its actual `Vehicle` and `Renter` objects, not
copied names and registration text, so two renters with the same name can never
be confused.

### Exceptions

- `IllegalArgumentException` for input that makes no sense (blank names, zero
  days, a vehicle not in the fleet).
- `VehicleStateException`, a custom unchecked exception, for a broken business
  rule (renting a car that is out, returning one that is not). It is unchecked
  because it is a request the depot must refuse, not a condition every caller
  should be forced to handle. It has its own type so the app catches exactly this
  refusal and nothing else.
- The domain never prints. `RentalTrackerApp` catches the refusal and
  `ReportPrinter` prints `Refused: ...`.

### Other decisions

- **No printing in the domain.** `Depot` returns results; `ReportPrinter` formats
  them and writes to the `PrintStream` it is given. This is also how the output
  stayed identical while everything around it was restructured.
- **Read-only lists.** Every list `Depot` hands out is an unmodifiable copy, so no
  caller can edit or clear the depot's records.
- **Rental numbers per depot.** The original used a static counter shared by
  every object in the program. Each `Depot` now numbers its own rentals.
- **Duplicate checks.** Two vehicles cannot share a registration (compared
  ignoring case, since `gt-1234-20` is the same plate). Two different renters
  *may* share a name, because two real people can.
- **Refusing invalid input is not a change of behaviour.** Every valid input
  behaves exactly as before. Inputs such as -2 days were never a feature; the
  original recorded them as a cost of GHS -300.0. They are now refused before any
  state changes.
- **Money stays `double`.** Switching to `BigDecimal` would change printed amounts
  (`450.0` would become `450.00`), which would break behaviour preservation. The
  rounding risk is noted in the diagnosis as a candidate for a future change.

### How the commercial van fits

The depot is adding a commercial-van class with its own deposit rules. In the
rebuilt design, that is **one new class**:

```java
public final class CommercialVan extends Vehicle {

    private static final String CATEGORY = "COMMERCIAL_VAN";

    public CommercialVan(String model, String reg, double dailyRate, double baseDeposit) {
        super(model, reg, dailyRate, baseDeposit);
    }

    @Override
    public double depositFor(Renter renter) {
        return getBaseDeposit(); // the van's own rule goes here
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }
}
```

| | Original design | Rebuilt design |
|---|---|---|
| Files or methods to edit | `Depot.rent()`: a third `if` branch, inside the method economy and premium depend on | **None** |
| Files to add | none, but the van exists only as a string that must be typed correctly everywhere | `CommercialVan.java` |
| Rules copied again | the member waiver and the whole rental step, a third time | **none**: the van reuses `costFor()`, and `standardDeposit()` if it needs the waiver |

I checked this by adding `CommercialVan` to a scratch copy of the code: it
compiled and rented correctly without editing any existing file.

The brief does not say whether members' deposits are waived for vans. The design
supports either answer: the van's `depositFor()` returns `standardDeposit(renter)`
to apply the waiver, or its own amount to ignore it. The example above assumes
vans always need a deposit.

### What changed from the design sketch

The structure matches the sketch in [DIAGNOSIS.md](DIAGNOSIS.md). Details added
while building:

- The custom exception is named `VehicleStateException`.
- Each vehicle class also supplies its category name (`getCategory()`), which
  replaced the free-text category field.
- The renter's free-text history became `Depot.rentalsFor(renter)`, answered from
  the real rental records, as the diagnosis planned.
- `Depot` gained duplicate and `null` checks, and the `getDepositsHeld()` query
  for the improvement.

## Improvement: deposits currently held

The end-of-day report now ends with the total deposit money the depot is
holding:

```text
Deposits held: GHS 500.0
```

**Why it matters to Dovewing:** casual renters pay a cash deposit, and members'
deposits are waived. At the end of the day, the desk needs to know how much
deposit money it should be holding, to check the cash against the cars that are
out. The original could not answer that, which is one more reason staff kept a
paper record.

**Why it can be trusted now:** the total is the sum of the deposits on active
rentals. That figure was meaningless in the original, where one car could have
two active rentals and a return could close both. Because the rebuild guarantees
one active rental per car and records each rental's deposit, the total is
correct.

**How it is built:** the rule (which rentals count, and the sum) lives in the
domain as `Depot.getDepositsHeld()`; `ReportPrinter` only prints it. A deposit is
assumed to be held while a rental is active and returned when the car comes back.
