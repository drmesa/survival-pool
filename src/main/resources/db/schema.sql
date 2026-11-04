CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(320) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE franchises (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_franchises_code UNIQUE (code)
);

CREATE TABLE rules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    rule_set_name VARCHAR(255) NOT NULL,
    entry_fee DECIMAL(10, 2) NOT NULL,
    strikes_allowed INTEGER NOT NULL,
    all_picks_before_week BOOLEAN NOT NULL,
    week_start_day VARCHAR(16) NOT NULL,
    reuse_teams BOOLEAN NOT NULL,
    ties_are_safe BOOLEAN NOT NULL,
    payout_split VARCHAR(32) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_rules_entry_fee CHECK (entry_fee >= 0),
    CONSTRAINT chk_rules_strikes_allowed CHECK (strikes_allowed > 0)
);

CREATE TABLE players (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_players_user UNIQUE (user_id),
    CONSTRAINT fk_players_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE seasons (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    number_of_weeks INTEGER NOT NULL,
    rules_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_seasons_rules FOREIGN KEY (rules_id) REFERENCES rules (id),
    CONSTRAINT chk_seasons_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_seasons_weeks CHECK (number_of_weeks > 0)
);

CREATE TABLE player_season_xref (
    id BIGINT NOT NULL AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    season_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_player_season UNIQUE (player_id, season_id),
    CONSTRAINT fk_player_season_player FOREIGN KEY (player_id) REFERENCES players (id),
    CONSTRAINT fk_player_season_season FOREIGN KEY (season_id) REFERENCES seasons (id)
);

CREATE TABLE payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    player_season_xref_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    paid_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_payments_player_season FOREIGN KEY (player_season_xref_id)
        REFERENCES player_season_xref (id),
    CONSTRAINT chk_payments_amount CHECK (amount > 0)
);
CREATE INDEX ix_payments_player_season ON payments (player_season_xref_id);

CREATE TABLE team_seasons (
    id BIGINT NOT NULL AUTO_INCREMENT,
    franchise_id BIGINT NOT NULL,
    season_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    abbreviation VARCHAR(32) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_team_seasons_franchise_season UNIQUE (franchise_id, season_id),
    CONSTRAINT fk_team_seasons_franchise FOREIGN KEY (franchise_id) REFERENCES franchises (id),
    CONSTRAINT fk_team_seasons_season FOREIGN KEY (season_id) REFERENCES seasons (id)
);

CREATE TABLE weeks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    season_id BIGINT NOT NULL,
    week_number INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_weeks_season_number UNIQUE (season_id, week_number),
    CONSTRAINT fk_weeks_season FOREIGN KEY (season_id) REFERENCES seasons (id),
    CONSTRAINT chk_weeks_number CHECK (week_number > 0),
    CONSTRAINT chk_weeks_date_range CHECK (TIMESTAMPDIFF(DAY, start_date, end_date) = 7)
);

CREATE TABLE games (
    id BIGINT NOT NULL AUTO_INCREMENT,
    week_id BIGINT NOT NULL,
    home_team_season_id BIGINT NOT NULL,
    away_team_season_id BIGINT NOT NULL,
    game_time DATETIME(6) NOT NULL,
    actual_start_time DATETIME(6),
    home_score INTEGER NOT NULL DEFAULT 0,
    away_score INTEGER NOT NULL DEFAULT 0,
    game_status VARCHAR(24) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_games_week FOREIGN KEY (week_id) REFERENCES weeks (id),
    CONSTRAINT fk_games_home_team FOREIGN KEY (home_team_season_id) REFERENCES team_seasons (id),
    CONSTRAINT fk_games_away_team FOREIGN KEY (away_team_season_id) REFERENCES team_seasons (id),
    CONSTRAINT chk_games_different_teams CHECK (home_team_season_id <> away_team_season_id),
    CONSTRAINT chk_games_home_score CHECK (home_score >= 0),
    CONSTRAINT chk_games_away_score CHECK (away_score >= 0)
);

CREATE TABLE picks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    player_season_xref_id BIGINT NOT NULL,
    week_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_picks_player_week UNIQUE (player_season_xref_id, week_id),
    CONSTRAINT fk_picks_player_season FOREIGN KEY (player_season_xref_id)
        REFERENCES player_season_xref (id),
    CONSTRAINT fk_picks_week FOREIGN KEY (week_id) REFERENCES weeks (id)
);

CREATE TABLE pick_submissions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pick_id BIGINT NOT NULL,
    team_season_id BIGINT NOT NULL,
    submitted_at DATETIME(6) NOT NULL,
    submitted_by_user_id BIGINT NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    correction_reason VARCHAR(1000),
    PRIMARY KEY (id),
    CONSTRAINT fk_pick_submissions_pick FOREIGN KEY (pick_id) REFERENCES picks (id),
    CONSTRAINT fk_pick_submissions_team FOREIGN KEY (team_season_id) REFERENCES team_seasons (id),
    CONSTRAINT fk_pick_submissions_user FOREIGN KEY (submitted_by_user_id) REFERENCES users (id)
);
CREATE INDEX ix_pick_submissions_pick_time ON pick_submissions (pick_id, submitted_at, id);
