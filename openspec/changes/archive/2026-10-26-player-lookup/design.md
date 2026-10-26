# Design

## Context

The application has a mutable `DomainPlayer` model and Spring MVC controllers. There is no player persistence or repository layer yet. See `proposal.md` for the feature motivation and `specs/player-lookup/spec.md` for API behavior.

## Goals / Non-Goals

**Goals:**
- Keep HTTP handling in the versioned controller package and domain-to-API adaptation in the service layer.
- Leave a clear seam for a future player lookup implementation without introducing persistence now.

**Non-Goals:**
- Define database mappings, repository interfaces, or persistence behavior.
- Version the service layer; only the HTTP controller and API object are versioned.

## Decisions

- The v1 controller depends on `PlayerService` and returns `ApiPlayer` directly. This keeps HTTP concerns separate from mapping and lets later API versions have their own controller and response types.
- The service exposes an optional API result and performs domain-to-API mapping after lookup. Its current lookup stub returns empty; a future persistence implementation can replace the stub without changing the controller contract.
- Use a dedicated `ApiPlayer` type rather than serializing `DomainPlayer`, so the public response shape can evolve independently of the domain model.

## Risks / Trade-offs

- The service currently always returns no result → the endpoint responds with 404 until a lookup implementation is added.
- `ApiPlayer` currently reuses mutable `DomainPick` types → nested API representation may need its own versioned type if pick fields evolve.
