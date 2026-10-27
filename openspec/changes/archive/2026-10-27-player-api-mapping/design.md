# Design

## Context

The existing player lookup service returns `Optional<ApiPlayer>` and performs field-by-field conversion from `DomainPlayer`. The v1 controller returns that API type directly. The response class is already located in `api.v1`, so its `Api` prefix repeats information provided by its package.

## Goals / Non-Goals

**Goals:**
- Keep the service independent of versioned API response types.
- Perform domain-to-response conversion at the controller boundary with MapStruct.
- Keep mapper classes in a dedicated `mapper` package.
- Preserve the player response fields and HTTP behavior, including the domain model's season-ID-keyed `pastPicks` structure.

**Non-Goals:**
- Add persistence or change the current stub lookup behavior.
- Change the `GET /players/{playerId}` route or response fields.
- Introduce separate API representations for nested pick values.

## Decisions

- Rename `ApiPlayer` to `Player` and keep it in `com.nothingtothetable.survivalpool.api.v1`.
- Add a `PlayerMapper` under `com.nothingtothetable.survivalpool.mapper`, declared as a MapStruct mapper with Spring component model. It maps `DomainPlayer` to the versioned `Player` response object.
- Have `PlayerService.findById` return `Optional<DomainPlayer>`. The controller maps the optional result through `PlayerMapper` before constructing the successful response.
- Configure MapStruct and its annotation processor in Maven. Include the Lombok-MapStruct binding in annotation processing so MapStruct can read Lombok-generated accessors.
- Keep the service stub returning empty; requests continue to produce `404 Not Found` until lookup is implemented.

## Risks / Trade-offs

- Renaming `ApiPlayer` is a source-level breaking change for any code importing that class, though the serialized response shape remains unchanged.
- The response continues to reuse `DomainPick`; introducing a dedicated versioned pick representation is outside this change.
- `pastPicks` is represented as a map keyed by season ID (`Long`), with each value containing that season's picks.
