# player-lookup Specification

## Purpose
Provide a versioned HTTP API for retrieving an individual survival-pool player by ID.

## Requirements

### Requirement: Retrieve a player by ID
The system SHALL provide `GET /players/{playerId}` to retrieve the player identified by `playerId`.

#### Scenario: Player is found
- **WHEN** a request is made for a player ID that the player service can find
- **THEN** the system returns `200 OK`
- **AND** the response contains a dedicated player API object with `id`, `name`, `strikes`, `currentPick`, and `pastPicks`

#### Scenario: Player is not found
- **WHEN** a request is made for a player ID that the player service cannot find
- **THEN** the system returns `404 Not Found`

### Requirement: Keep lookup behind a service boundary
The version 1 player controller SHALL delegate player lookup to a service and SHALL NOT depend directly on database or repository interfaces. The service SHALL return the version 1 player API object, adapting the domain player when a lookup implementation is available.

#### Scenario: Lookup is not implemented yet
- **WHEN** the initial stub service receives a player lookup request
- **THEN** it reports that no player was found
- **AND** the controller returns `404 Not Found`

#### Scenario: Player is adapted for the API
- **WHEN** the service finds a domain player
- **THEN** it adapts the domain player to the version 1 player API object
- **AND** the controller returns that API object with `200 OK`
