# Design notes — Dovewing Car Rentals rebuild

The key design decisions, the alternatives I rejected, how the coming commercial
van fits, what changed from the sketch in [DIAGNOSIS.md](DIAGNOSIS.md), and the
improvement.

## 1. The central rule: a vehicle is available or rented, never both

In the original, whether a car was out was stored in two places (`Vehicle.status`
and each `Rental.status`) and nothing kept them in step. Now:

- `Vehicle` **owns** its status. Its only state changes are the package-private
  `markRented()` and `markReturned()`, and each refuses an invalid change by
  throwing `VehicleStateException`. The guard sits where the state lives, so no
  caller can rent a car twice.
- `Depot` is the only class that calls them, and it changes the vehicle and its
  rental **together, in one method**.
- Each method **checks first, then changes, then records.** In `rent()`,
  `markRented()` is the last check and the first change, so a refused rental
  leaves no trace: no rental, no record, no rental number used.

**Alternative rejected:** no stored status, deriving it from "is there an active
rental for this car?". That also removes the second source of truth, but every
status question would go through `Depot`, and the rule would no longer live with
the object it protects.

## 2. Vehicle classes: an abstract class with subclasses

`Vehicle` is abstract and holds the shared state (model, registration, daily rate,
deposit, status). `EconomyVehicle` and `PremiumVehicle` each implement
`depositFor(Renter)` and `getCategory()`. `Depot.rent()` asks the vehicle for
`costFor(days)` and `depositFor(renter)`, and Java runs the right version for the
actual vehicle, so `rent()` no longer branches on category.

- `costFor()` is `final`: one cost rule for every vehicle.
- The member waiver lives once, in the `protected final` helper `standardDeposit()`.
- `depositFor()` is abstract, so every vehicle class must decide its deposit rule
  or it will not compile.
- The subclasses are `final`: a van cannot be built by extending Premium to reuse
  its code, because a van is not a premium car.

**Alternatives rejected:**

- **An enum plus a `switch` in `rent()`.** The compiler checks every case, but each
  new class still means editing the central rental method, which is how "a tweak
  to premium pricing once broke how economy returns were handled".
- **A deposit-rule object each vehicle has (composition).** Defensible, and better
  if rules changed at runtime or varied independently of the vehicle class. Here
  the rule belongs to the class itself, and the abstract class also gives one home
  for the shared state and the status rule.

## 3. Other key decisions

- **`RenterType` enum with behaviour.** Each constant knows whether its deposit is
  waived, so a mistyped renter type is a compile error, not a member silently
  charged a deposit.
- **`Rental` is a class, not a record.** A record suits an immutable value; a
  rental's status changes when the car comes back. It holds its real `Vehicle` and
  `Renter`, so two renters with the same name can never be confused.
- **Two kinds of exception.** `IllegalArgumentException` for input that makes no
  sense; `VehicleStateException` (custom, unchecked) for a broken business rule.
  It is unchecked because it is a request the depot refuses, not a condition every
  caller should be forced to handle, and it has its own type so the app catches
  exactly this refusal.
- **No printing in the domain.** `Depot` returns results; `ReportPrinter` formats
  them and writes to the `PrintStream` it is given. This is also how the output
  stayed identical while everything else was restructured.
- **Read-only lists.** Every list `Depot` returns is an unmodifiable copy.
- **Rental numbers per depot,** replacing a static counter shared by the program.
- **Duplicates.** Two vehicles cannot share a registration (compared ignoring
  case); two different renters may share a name, because two real people can.
- **Refusing invalid input is not a change of behaviour.** Every valid input
  behaves exactly as before. Inputs such as -2 days were never a feature; the
  original recorded them as a cost of GHS -300.0.
- **Money stays `double`.** `BigDecimal` would change printed amounts (`450.0`
  becomes `450.00`) and break behaviour preservation; the rounding risk is noted
  in the diagnosis for a future change.

## 4. How the commercial van fits

The depot is adding a commercial-van class with its own deposit rules. In this
design, that is **one new class**:

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
| Files to add | none; the van exists only as a string that must be typed correctly | `CommercialVan.java` |
| Rules copied again | the member waiver and the whole rental step, a third time | **None**: it reuses `costFor()`, and `standardDeposit()` if it needs the waiver |

I checked this by adding `CommercialVan` to a scratch copy of the code: it
compiled and rented correctly without editing any existing file. The brief does
not say whether members' deposits are waived for vans; the example assumes vans
always need one. Returning `standardDeposit(renter)` instead would apply the
waiver.

## 5. What changed from the design sketch

The structure matches the sketch in [DIAGNOSIS.md](DIAGNOSIS.md). Added while
building: the exception is named `VehicleStateException`; each vehicle class
supplies its category name; the renter's free-text history became
`Depot.rentalsFor(renter)`, read from the real rentals; `Depot` gained duplicate
and `null` checks, and `getDepositsHeld()` for the improvement.

## 6. Improvement: deposits currently held

The end-of-day report now ends with `Deposits held: GHS 500.0`.

**Why it matters to Dovewing:** casual renters pay a cash deposit; members' are
waived. At the end of the day, the desk needs to know how much deposit money it
should be holding, to check the cash against the cars that are out. The original
could not answer that, one more reason staff kept a paper record.

**Why it can be trusted now:** in the original, one car could have two active
rentals and a return could close both, so any total would be wrong. The rebuild
guarantees one active rental per car and records each rental's deposit.

**How it is built:** the rule (which rentals count, and the sum) lives in the
domain as `Depot.getDepositsHeld()`; `ReportPrinter` only prints it. A deposit is
assumed to be held while the rental is active and returned with the car.
