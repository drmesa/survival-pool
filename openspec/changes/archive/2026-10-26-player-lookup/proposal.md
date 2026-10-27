## Why

The application needs an HTTP endpoint for retrieving an individual player, but it does not have player persistence or a lookup implementation yet. Establishing the controller, response contract, and service boundary now allows the API to take shape without introducing database interfaces prematurely.

## What Changes

- Add `GET /players/{playerId}` to retrieve a player by ID.
- Add a dedicated player response API object containing the current player fields: `id`, `name`, `strikes`, `currentPick`, and `pastPicks`.
- Route player lookup through a service layer. The initial service implementation is a stub and reports no player found until lookup is implemented.
- Return `404 Not Found` when the service cannot find the requested player.

## Capabilities

### New Capabilities
- `player-lookup`: Retrieve an individual player's API representation by ID.

### Modified Capabilities
- None.

## Impact

- Adds a player controller, response API object, and service implementation.
- Does not add database repositories, database interfaces, or persistence behavior.
- The service stub returns no result, so the endpoint returns `404 Not Found` for all IDs until player lookup is implemented.
