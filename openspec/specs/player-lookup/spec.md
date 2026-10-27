# player-lookup Specification

## Purpose
Provide a versioned HTTP API for retrieving an individual survival-pool player by ID.

## Requirements

### Requirement: Retrieve a player by ID
The system SHALL provide `GET /players/{playerId}` to retrieve the player identified by `playerId`.

#### Scenario: Player is found
- **WHEN** a request is made for a player ID that the player service can find
- **THEN** the system returns `200 OK`
- **AND** the response contains a dedicated `Player` API object in the version 1 API package with `id`, `name`, `strikes`, `currentPick`, and `pastPicks`
- **AND** `pastPicks` is a map keyed by season ID (`Long`), with each value containing that season's picks

#### Scenario: Player is not found
- **WHEN** a request is made for a player ID that the player service cannot find
- **THEN** the system returns `404 Not Found`

### Requirement: Keep lookup behind a service boundary
The version 1 player controller SHALL delegate player lookup to a service and SHALL NOT depend directly on database or repository interfaces. The service SHALL return an optional domain player. The controller SHALL use a MapStruct mapper from the dedicated mapper package to adapt a found domain player to the version 1 `Player` API object.

#### Scenario: Lookup is not implemented yet
- **WHEN** the initial stub service receives a player lookup request
- **THEN** it reports that no player was found
- **AND** the controller returns `404 Not Found`

#### Scenario: Controller maps a found player for the API
- **WHEN** the service finds a domain player
- **THEN** the controller maps the domain player to the version 1 `Player` API object
- **AND** the controller returns that API object with `200 OK`
