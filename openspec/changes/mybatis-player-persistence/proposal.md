# Proposal

## Why

Player lookup currently has no persistence implementation, so the existing endpoint cannot return players stored in the configured SQL database. A MyBatis repository will provide a small, testable database boundary while preserving the existing player API contract.

## What Changes

- Add MyBatis integration with the existing MySQL datasource and a repository mapper for `players(id, name, strikes)`.
- Connect player lookup to the repository, mapping stored rows to `DomainPlayer` and preserving the existing not-found response.
- Add tests for repository-backed player lookup without requiring a live database for service tests.
- Use a currently supported MyBatis starter compatible with the project's Spring Boot release.
- Evaluate MySQL Connector/J 26.10.0 as an override to the Spring Boot-managed version; retain it only if compatibility checks pass. Keep the current Spring Boot release rather than upgrading to a release candidate.
- Leave pick and season persistence out of scope until their SQL schema and relationships are defined.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

None. This change replaces the stub implementation without changing the existing player lookup API contract. The change is marked `skip_specs: true`.

## Impact

- Maven dependencies and Spring/MyBatis mapper configuration.
- A new player repository and the existing player service.
- Existing MySQL database, using the agreed `players` table.
- Unit tests for service behavior and compatibility validation for the selected SQL driver.
- No HTTP endpoint or response-shape changes.
