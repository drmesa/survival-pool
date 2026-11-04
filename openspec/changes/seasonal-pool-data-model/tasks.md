# Tasks

## 1. Define the persistence model

- [x] 1.1 Add domain types for season rules, player-season participation, payments, franchises, season teams, weeks, games, picks, and pick-submission history.
- [x] 1.2 Update `DomainPlayer` to contain only player identity and its user ID; update the player mapper query and test table accordingly.
- [x] 1.3 Add the SQL schema resource with foreign keys, uniqueness constraints, and no automatic schema initialization.

## 2. Implement player-season reads

- [x] 2.1 Replace the old player response with a `PlayerPage` response containing player identity, current-season picks, derived strikes, and paid-in status.
- [x] 2.2 Add repository queries and service assembly for the current season's player participation, pick history, game outcomes, and payments.
- [x] 2.3 Calculate strikes, pick lock/reuse eligibility, and payment threshold from the season's rules and source records.

## 3. Verify

- [x] 3.1 Add unit tests for derived strike, tie, missed-pick, reuse, correction, and payment behavior.
- [x] 3.2 Add mapper/schema tests for foreign-key mapping and uniqueness constraints.
- [x] 3.3 Run the targeted tests and full Maven test suite.
