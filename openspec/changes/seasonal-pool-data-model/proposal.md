# Proposal

## Why

The current domain model mixes player identity with season-specific state, and the persistence model does not yet describe the relationships needed to support seasons, payments, NFL teams, games, picks, and pick history. Define a season-oriented model before expanding persistence so historical data remains accurate and derived status does not drift from its source records.

## What Changes

- Define `User` and `Player` as distinct concepts, linked by a required, unique `user_id` foreign key on `Player`: each player belongs to exactly one user, and each user can have at most one player. Keep `Player` identity limited to `id`, `name`, and `userId`.
- Define `Season` with `id`, display `name`, `startDate`, `endDate`, `numberOfWeeks`, and `rulesId`.
- Define reusable, typed `Rules` settings with `id`, a human-readable name, and settings including `entryFee`, `strikesAllowed`, `allPicksBeforeWeek`, `weekStartDay`, `reuseTeams`, `tiesAreSafe`, and a named payout split. Treat a ruleset as immutable once used by a season.
- Define `PlayerSeasonXref` as the unique association between a player and a season. Derive strikes from picks, game results, and rules instead of storing a strike count.
- Define `Payment` rows linked to `PlayerSeasonXref`, with an amount and paid-at timestamp. A player is paid in when recorded payments meet or exceed that season's entry fee; partial payments remain unpaid and overpayments are allowed.
- Define `Franchise` as an enum with an explicit stable database ID and unique code (for example, `BROWNS_1946` or `RAVENS_1996`), and seed the `franchises` table from those IDs and codes. Define `TeamSeason` with `franchiseId`, `seasonId`, season-specific full name, and abbreviation; enforce one row per franchise per season.
- Define regular-season `Week` rows with `seasonId`, `weekNumber`, and a seven-day date range (`startDate` inclusive, `endDate` exclusive), unique per season and week number. Derive the range from the season start and `weekStartDay`. Define `Game` rows referencing a week and home/away `TeamSeason`, with game time, scores initialized to 0–0, and explicit status (`SCHEDULED`, `IN_PROGRESS`, `FINAL`, `POSTPONED`, or `CANCELLED`). The database rejects games outside their week's date range and week range updates that would exclude existing games. Derive the outcome from final status and scores.
- Define `Pick` as one player-season/week slot, unique per player-season and week, and `PickSubmission` as append-only selection history. Each history row records the selected `TeamSeason`, submission time, submitting user, and event type; admin corrections require a reason and may change the effective pick after lock while retaining prior history.
- Keep player-page data as a composed response containing player identity, current-season picks, derived strikes, and derived paid-in status rather than persisting those derived values on `Player`.

## Pool Rules

- A player is eliminated when their strike count reaches `strikesAllowed`; a safe tie does not add a strike when `tiesAreSafe` is enabled.
- A missed pick adds a strike. With `allPicksBeforeWeek` enabled, a pick must be submitted before the first game on `weekStartDay` starts; if there is no game that day, the deadline is 11:59 p.m. Eastern on that day. Otherwise, a pick is required at some point during the week.
- `weekStartDay` defines regular-season weeks in Eastern time: games from that day through the day before the next `weekStartDay` belong to the same week.
- When `allPicksBeforeWeek` is disabled, a pick locks when its selected team's game starts. Postponed or cancelled games do not lock a pick until the game actually starts. A week with no submitted pick still counts as a strike, including when games are postponed or cancelled.
- When team reuse is disabled, a team becomes used only after its game starts. When enabled, team reuse is permitted.
- The named payout split is a pool rule; outgoing prize payments are outside the incoming `Payment` model.

## Capabilities

### New Capabilities
- `seasonal-pool-data`: Define the season-oriented pool records, relationships, and derived player-season state needed to manage participation, payment, teams, games, and picks.

### Modified Capabilities
- `player-lookup`: Replace the identity-plus-season-state shape with a player-page response composed from player identity and current-season data.

## Impact

- Domain types for users, players, seasons, rules, player-season participation, payments, franchises, season teams, weeks, games, picks, and pick submissions.
- MyBatis repositories and database schema/migrations for the agreed relationships and constraints.
- Player-page service and API response mapping for picks, strikes, and paid-in status.
- Tests for relational mappings, history and correction behavior, game/pick locking, strike calculation, team reuse, and payment threshold behavior.
- No production schema migration or API implementation is part of this proposal; those are implementation work after the proposal is accepted.
