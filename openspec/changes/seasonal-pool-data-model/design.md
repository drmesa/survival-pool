# Design

## Context

The existing domain types combine stable player identity with season-specific pool state, and several types model relationships as nested objects. The new model gives persistent records explicit IDs and foreign-key IDs, while page-specific values are assembled from season participation, picks, game results, and payments.

## Decisions

- Keep `Player` as a user-linked identity (`id`, `userId`, and `name`). Keep current-season values in a `PlayerPage` API response.
- Use `PlayerSeasonXref` as the anchor for participation, payments, and picks. Do not persist strikes or payment status.
- Store `Season.rulesId` and structured season dates and length. Keep reusable pool settings in immutable `Rules` rows.
- Represent NFL identity with explicit stable IDs and unique codes in the `Franchise` enum, mirrored by `db/seed-franchises.sql`. Store season-specific display name and abbreviation in `TeamSeason`.
- Represent a schedule as regular-season `Week` rows belonging to a season and `Game` rows belonging to a week. Each week stores an inclusive start date and exclusive end date spanning seven days; database triggers reject games outside that range and week updates that would exclude existing games.
- Store a pick slot once per player-season/week. Store every accepted submission and administrative correction as a separate `PickSubmission` record; corrections require a reason and identify the acting user.
- Store timestamps as instants and interpret week boundaries and deadlines in `America/New_York`.
- Store timestamp values in SQL `DATETIME(6)` columns as Eastern local time; application/JDBC configuration uses `America/New_York` consistently with the league's deadlines.
- Provide SQL schema and constraint resources, but do not enable automatic production schema initialization. Apply `db/schema.sql` and then `db/week-game-date-constraints.sql` explicitly during deployment.

## Risks / Trade-offs

- Calculating status requires joining several records and can be more expensive than storing cached values. Derived values remain authoritative; optimize with batched queries or a read model only if measurements require it.
- The status of a postponed game and its actual start must be represented consistently by schedule updates. The current game record stores the effective start time used for pick locking.
- An enum-backed franchise requires an application release to introduce a new franchise. Its explicit ID and code must stay aligned with the seed SQL; season-specific labels remain data.
