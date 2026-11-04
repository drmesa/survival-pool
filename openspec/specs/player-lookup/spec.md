# player-lookup Specification

## Purpose
Provide a versioned HTTP API for retrieving a player's identity and current-season pool status.

## Requirements

### Requirement: Retrieve a player page by ID
The system SHALL provide `GET /players/{playerId}` to retrieve the player page for the identified player.

#### Scenario: Player page is found
- **WHEN** a request is made for a player ID that the player-page service can find
- **THEN** the system returns `200 OK`
- **AND** the response contains the player's `id` and `name`
- **AND** the response contains an optional current-season view with picks, derived strikes, and paid-in status

#### Scenario: Player is not found
- **WHEN** a request is made for a player ID that the player-page service cannot find
- **THEN** the system returns `404 Not Found`

### Requirement: Keep lookup behind a service boundary
The version 1 player controller SHALL delegate player-page assembly to a service and SHALL NOT depend directly on database or repository interfaces. The service SHALL return an optional domain player page. The controller SHALL use a MapStruct mapper from the dedicated mapper package to adapt a found domain page to the version 1 `PlayerPage` API object.

#### Scenario: Controller maps a found player page for the API
- **WHEN** the service finds a domain player page
- **THEN** the controller maps it to the version 1 `PlayerPage` API object
- **AND** the controller returns that API object with `200 OK`
