## 1. Add MapStruct mapping

- [x] 1.1 Add MapStruct and annotation processor configuration to the Maven build, including Lombok-MapStruct binding.
- [x] 1.2 Add `PlayerMapper` in the dedicated mapper package to map `DomainPlayer` to `api.v1.Player`.
- [x] 1.3 Rename `ApiPlayer` to `Player` without changing the response fields.

## 2. Move mapping to the controller boundary

- [x] 2.1 Change `PlayerService.findById` to return `Optional<DomainPlayer>` and remove API mapping from the service.
- [x] 2.2 Inject `PlayerMapper` into `PlayerController` and map successful service results to the API response.
- [x] 2.3 Update controller tests to verify mapped response fields and unchanged 404 behavior.
- [x] 2.4 Verify no references to `ApiPlayer` remain and run the relevant Maven tests.
