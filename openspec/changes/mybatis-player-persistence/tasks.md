# Tasks

## 1. Add MyBatis and mapper persistence

- [x] 1.1 Add MyBatis Spring Boot Starter and its test starter at 4.1.0, plus test-scoped H2; verify Maven resolves the dependencies with `mvn dependency:tree`.
- [x] 1.2 Add a scanned MyBatis player mapper that selects `id`, `name`, and `strikes` by parameterized player ID; add an H2 MyBatis slice test and verify found-row mapping and missing-row behavior with `mvn test`.

## 2. Connect player lookup through the service

- [x] 2.1 Inject the player mapper into `PlayerService` and convert missing rows to `Optional.empty()`; add unit tests for found and missing players and verify them with `mvn test`.

## 3. Verify application integration

- [x] 3.1 Run the full Maven test suite and verify mapper configuration and SQL execution through the H2 MyBatis slice test.
- [x] 3.2 Confirm the resolved MySQL Connector/J version is 26.10.0 and review the final diff to ensure no API contract or production schema changes.

## Workflow follow-up

- Archive the change after implementation review and validation.
