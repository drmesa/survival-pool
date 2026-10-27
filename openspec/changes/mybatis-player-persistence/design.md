# Design

## Context

See `proposal.md` for motivation and scope. The application is a Spring Boot 4.1.2 Maven project with a MySQL `DataSource`, a `DomainPlayer` type, and a `PlayerService` stub. The configured database credentials are provided by application properties; database schema files are not currently present.

## Goals / Non-Goals

**Goals:**
- Keep SQL access behind an injectable MyBatis mapper and preserve the service/controller boundary.
- Map only the agreed player columns to the existing domain type.
- Verify mapper SQL and service behavior without requiring a running MySQL server.

**Non-Goals:**
- Change the HTTP contract or persist pick/season associations.
- Create or migrate the production MySQL schema.
- Change the Spring Boot release.

## Decisions

- Use `org.mybatis.spring.boot:mybatis-spring-boot-starter:4.1.0`. The 4.1 line targets Spring Boot 4.1 and Java 17+, matching the project's Boot 4.1.2 and Java 27. Use the latest compatible 4.1 release rather than 4.0.0, which targets the Boot 4.0 line.
- Register the repository package explicitly with `@MapperScan` on the application class. The application entry point is in the default Java package, so relying on package-relative mapper discovery risks scanning the wrong package; explicit scanning keeps registration deterministic.
- Implement `findById` with a parameterized annotation query selecting `id`, `name`, and `strikes` from `players`. MyBatis maps those matching column names to `DomainPlayer` properties; a missing row returns `null`, which the service converts to `Optional.empty()`.
- Keep `PlayerService` as the application boundary and inject the mapper through its constructor. The controller remains unchanged and continues to own API mapping and 404 behavior.
- Cover SQL mapping with a MyBatis slice test using H2 in MySQL compatibility mode and a test-only `players` table. Use isolated Mockito-based service tests for Optional conversion. This checks actual mapper execution without binding tests to developer credentials or a running server.
- Keep MySQL Connector/J 26.10.0 pinned as established during the dependency security fix; the Boot BOM version was identified as affected by the advisories listed in `proposal.md`. Do not upgrade Spring Boot to a release candidate.

## Risks / Trade-offs

- [The deployed `players` table may use different names or types] → Confirm the existing schema before deployment; select explicit columns and avoid creating or altering production tables in this change.
- [H2 MySQL mode is not a complete substitute for MySQL] → Limit integration coverage to the portable select and row mapping; verify against the deployed database before release.
- [Connector/J 26.10.0 is a major version-line override from the Boot BOM] → Keep the version explicit, run the complete application tests, and perform a deployment smoke test against the configured MySQL server.
- [Current pick and past picks remain unset by this query] → Keep them out of scope until their tables and relationships are defined; do not imply those associations are loaded.

## Migration Plan

1. Deploy the application with the MyBatis mapper and driver version after confirming the existing `players` schema contains `id`, `name`, and `strikes`.
2. Verify a known player lookup and a missing ID against the target MySQL database.
3. If driver compatibility fails, roll back the driver pin independently while retaining or reverting the mapper change as needed. No database migration or destructive schema operation is part of this change.
