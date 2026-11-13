# Dovewing Car Rentals — Fleet and Rental Tracker (rebuilt)

Dovewing's front desk uses this program to track its fleet and rentals. The
original worked, but staff kept a paper backup because the same car could show
as rented to two people at once. This repository rebuilds it: the same features
and results, reorganised so the rules cannot be broken from outside and a new
vehicle class can be added without a rewrite.

| Document | What it holds |
|---|---|
| [DIAGNOSIS.md](DIAGNOSIS.md) | Ranked defects and the design sketch, committed in `e734fd1` before the first code change (`19f606c`) |
| [DESIGN.md](DESIGN.md) | Design notes: key decisions, alternatives, the commercial van, and the improvement |
| [docs/original-output.txt](docs/original-output.txt) | Output of the original program, captured before any change |
| [docs/rebuilt-output.txt](docs/rebuilt-output.txt) | Output of the rebuilt program |

## Running it

You need **JDK 21**. The program has no runtime dependencies; JUnit is used for
the tests only.

**With Maven** (from the project root):

```bash
mvn -q compile exec:java
```

**Or directly with the JDK** (from the project root, in a bash shell such as Git
Bash on Windows, because `$(find ...)` is bash syntax):

```bash
javac -d target/classes $(find src/main/java -name "*.java")
java -cp target/classes com.dovewing.app.RentalTrackerApp
```

`javac -d` creates `target/classes` if it does not exist.

## Running the tests

```bash
mvn -q test
```

- `DepotTest` checks the business rules: cost, deposit and member waiver, the
  refusal of a vehicle that is already out (with no trace left behind), returns,
  input validation, read-only lists, and the deposits-held total.
- `RentalTrackerAppTest` runs the program and checks that its output matches
  `docs/rebuilt-output.txt` exactly, so any change in behaviour fails the build.

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
src/test/java/com/dovewing/     DepotTest, RentalTrackerAppTest
```

## Behaviour preserved

The rebuild keeps every original feature and result: renting, returning, cost
(daily rate × days), deposit (the vehicle's deposit, waived for members), and the
end-of-day report, with every original line in the same format.

Compared with the original, the output differs in two ways, and only these two:

1. **One existing behaviour changes, as the brief requires:** a vehicle that is
   already out can no longer be rented, so it can never be available and rented
   at the same time.
2. **One line is added** at the end of the report by the improvement. No other
   line changes.

The complete diff, exactly as `git diff --no-index docs/original-output.txt docs/rebuilt-output.txt` prints it:

```diff
@@ -1,7 +1,7 @@
 Rented Toyota Vitz (GT-1234-20) to Kofi Mensah for 3 days, cost GHS 450.0, deposit GHS 500.0
 Rented Toyota Prado (GR-5678-22) to Ama Owusu for 2 days, cost GHS 1200.0, deposit GHS 0.0
 Returned Toyota Prado (GR-5678-22)
-Rented Toyota Vitz (GT-1234-20) to Ama Owusu for 2 days, cost GHS 300.0, deposit GHS 0.0
+Refused: Toyota Vitz (GT-1234-20) is already rented
 
 === Dovewing Car Rentals - end of day ===
 Fleet status:
@@ -9,4 +9,4 @@ Fleet status:
   Toyota Prado (GR-5678-22, PREMIUM): AVAILABLE
 Active rentals:
   Rental #1: GT-1234-20 -> Kofi Mensah (3 days)
-  Rental #3: GT-1234-20 -> Ama Owusu (2 days)
+Deposits held: GHS 500.0
```

| Line | Why it changed |
|---|---|
| `Rented Toyota Vitz ... to Ama Owusu` becomes `Refused: ...` | The Vitz is already out with Kofi. This is the rule the business says must never be broken. |
| `Rental #3` is gone | The refused rental is never created, so the Vitz has one active rental instead of two, and no rental number is used up. |
| `Deposits held: GHS 500.0` is new | The improvement, appended as the last line. |

**How this was checked:** after every refactoring commit, the output was compared
with `docs/original-output.txt` and had to be identical. The one behaviour change
was made in its own commit (`75a4042`, "fix: refuse renting a vehicle that is
already rented"), which recorded `docs/rebuilt-output.txt`; later commits were
checked against that file. `RentalTrackerAppTest` now makes this check automatic.

### Protection: what outside code can no longer do

Each of these was tried from outside the domain package and is refused. The
runtime cases are covered by `DepotTest`.

| Attempt | Result |
|---|---|
| Set a vehicle's status directly | Does not compile: the field is private |
| Call `markRented()` / `markReturned()` from outside the domain | Does not compile: package-private |
| Create a `Rental` without going through the depot | Does not compile: package-private constructor |
| `new Vehicle(...)` | Does not compile: `Vehicle` is abstract |
| Clear or edit any list `Depot` returns | `UnsupportedOperationException`: read-only copies |
| Rent a vehicle that is already out, or return one that is not | `VehicleStateException` |
| Rent for 0 days, or a vehicle or renter not in this depot | `IllegalArgumentException`, before anything changes |
| Add a second vehicle with the same registration | `IllegalArgumentException` |
| Blank names, a non-positive rate, a negative deposit, `null` arguments | Refused when the object is created or the method is called |
