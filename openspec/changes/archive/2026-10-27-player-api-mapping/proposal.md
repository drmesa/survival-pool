## Why

The player service currently performs HTTP response mapping, coupling the service layer to the versioned API representation. Mapping belongs at the controller boundary, where a dedicated MapStruct mapper can adapt domain data into the response contract. The `Api` prefix in `ApiPlayer` is also redundant because the class already lives in the `api.v1` package.

## What Changes

- Add a MapStruct mapper in a dedicated `mapper` package to convert `DomainPlayer` into the version 1 `Player` response object.
- Change `PlayerService` to return an optional domain player rather than an API response object.
- Inject the mapper into the v1 player controller and map found domain players before returning them.
- Rename `ApiPlayer` to `Player` while keeping the response object in `api.v1`.
- Preserve the season-ID-keyed map structure of `pastPicks` in the API response.
- Add or update tests to verify the controller response and the service/domain boundary.

## Capabilities

### Modified Capabilities
- `player-lookup`: the service returns a domain player, and the controller maps it to the API response using MapStruct.

## Impact

- Adds MapStruct and its annotation processor to the Maven build.
- Changes the player service/controller contract and renames the existing response class.
- Does not change the endpoint path or not-found behavior; `pastPicks` reflects the domain model's season-ID-keyed map structure.
