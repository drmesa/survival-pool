# Seasonal Pool Data Specification

## Purpose

Define season-oriented persistence for player participation and NFL survival-pool records.

## Requirements

### Requirement: Keep player identity separate from season participation
The system SHALL store a player identity with `id`, `name`, and a required, unique `userId` reference. Season-specific picks, strikes, and payment status SHALL NOT be stored on the player identity.

#### Scenario: Player belongs to a user
- **WHEN** a player record is persisted
- **THEN** it references exactly one user
- **AND** a user may have no player or at most one player

### Requirement: Preserve season-specific pool participation
The system SHALL represent participation with a `PlayerSeasonXref` linking one player to one season, unique for that pair. Payments and pick slots SHALL reference the participation record.

#### Scenario: Player participates once in a season
- **WHEN** a player joins a season
- **THEN** the system creates one player-season association
- **AND** a duplicate association for that player and season is rejected

### Requirement: Derive payment status
The system SHALL record payments with an amount and paid-at timestamp. Paid-in status SHALL be true when the sum of recorded payments is greater than or equal to the season rules' entry fee.

#### Scenario: Partial and excess payment
- **WHEN** recorded payments total less than the entry fee
- **THEN** the player is not paid in
- **WHEN** recorded payments meet or exceed the entry fee
- **THEN** the player is paid in, including when the total exceeds the fee

### Requirement: Preserve franchise identity and season labels
The system SHALL represent each franchise with an explicit stable ID and unique code in the `Franchise` enum, with matching rows seeded into the `franchises` table. Each `TeamSeason` SHALL reference one franchise and one season, and store the full name and abbreviation used for that season.

#### Scenario: Historical team label remains unchanged
- **WHEN** a franchise's location or display name changes in a later season
- **THEN** prior `TeamSeason` records retain their historical name and abbreviation

### Requirement: Model regular-season schedule and games
The system SHALL represent each regular-season week by season ID and week number, unique within a season, and store its valid date range as a seven-day interval with an inclusive start and exclusive end. The range SHALL be derived from the season start date and configured `weekStartDay`. Each game SHALL reference its week and home and away season teams and have scores and an explicit game status. A game's scheduled Eastern calendar date SHALL fall within the referenced week's range; changing a week range SHALL NOT exclude an existing game.

#### Scenario: Game scheduled outside its week is rejected
- **WHEN** a game is inserted or updated with a scheduled date before the week's start date or on or after its end date
- **THEN** the database rejects the write

#### Scenario: Week range must match season rules
- **WHEN** a week is inserted or updated with dates that do not match its season start date and configured `weekStartDay`
- **THEN** the database rejects the write

#### Scenario: Week range cannot exclude existing games
- **WHEN** a week date range is changed so that an existing game's scheduled date falls outside it
- **THEN** the database rejects the update

#### Scenario: Game status distinguishes an unplayed game from a tie
- **WHEN** a scheduled game has a 0-0 score
- **THEN** its status indicates that it is not final
- **WHEN** a final game has equal scores
- **THEN** its outcome is a tie derived from the scores

### Requirement: Preserve pick submissions and corrections
The system SHALL store at most one pick slot per player-season/week and SHALL retain every submission as a separate history record containing the selected season team, timestamp, actor, and event type. An administrative correction SHALL include a reason and SHALL preserve prior history while changing the effective selection.

#### Scenario: Correct a locked pick
- **WHEN** an administrator corrects a locked pick with a reason
- **THEN** the corrected team becomes the effective selection
- **AND** the original and correction remain in the history

### Requirement: Derive strikes and pick eligibility from rules
The system SHALL derive strikes from pick submissions, schedule/game results, and the season's rules. A player SHALL be eliminated when strikes reach the configured allowance. A missed pick SHALL count as a strike; a tie SHALL be safe only when configured as safe.

#### Scenario: All picks are due before the week
- **WHEN** `allPicksBeforeWeek` is enabled
- **THEN** picks lock at the first game start on `weekStartDay`
- **AND** if no game is scheduled that day, the deadline is 11:59 p.m. Eastern on that day

#### Scenario: Picks lock at the selected game's start
- **WHEN** `allPicksBeforeWeek` is disabled
- **THEN** a pick locks when its selected team's game starts
- **AND** a postponed or cancelled game that has not started does not lock the pick

#### Scenario: Team reuse is disabled
- **WHEN** a player's selected team's game has not started
- **THEN** the team is not yet considered used
- **AND** after the game starts, reuse is disallowed when `reuseTeams` is disabled
