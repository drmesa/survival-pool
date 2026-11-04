-- Apply with a MySQL client after schema.sql; DELIMITER is a client command.
DELIMITER $$

CREATE TRIGGER seasons_week_dates_before_update
BEFORE UPDATE ON seasons
FOR EACH ROW
BEGIN
    IF (NEW.start_date <> OLD.start_date OR NEW.rules_id <> OLD.rules_id)
       AND EXISTS (SELECT 1 FROM weeks WHERE season_id = OLD.id) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Season start date and rules cannot change after weeks are created';
    END IF;
END$$

CREATE TRIGGER rules_week_dates_before_update
BEFORE UPDATE ON rules
FOR EACH ROW
BEGIN
    IF NEW.week_start_day <> OLD.week_start_day
       AND EXISTS (
           SELECT 1
           FROM seasons s
           JOIN weeks w ON w.season_id = s.id
           WHERE s.rules_id = OLD.id
       ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Week start day cannot change after season weeks are created';
    END IF;
END$$

CREATE TRIGGER weeks_date_before_insert
BEFORE INSERT ON weeks
FOR EACH ROW
BEGIN
    DECLARE season_start DATE;
    DECLARE target_weekday INTEGER;
    DECLARE expected_start DATE;

    SELECT s.start_date, FIELD(r.week_start_day, 'MONDAY', 'TUESDAY', 'WEDNESDAY',
                               'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') - 1
    INTO season_start, target_weekday
    FROM seasons s
    JOIN rules r ON r.id = s.rules_id
    WHERE s.id = NEW.season_id;

    IF target_weekday < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Season has an invalid week start day';
    END IF;

    SET expected_start = DATE_ADD(
        season_start,
        INTERVAL (MOD(target_weekday - WEEKDAY(season_start) + 7, 7)
                  + (NEW.week_number - 1) * 7) DAY
    );

    IF NEW.start_date <> expected_start
       OR NEW.end_date <> DATE_ADD(expected_start, INTERVAL 7 DAY) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Week date range does not match the season schedule';
    END IF;
END$$

CREATE TRIGGER weeks_date_before_update
BEFORE UPDATE ON weeks
FOR EACH ROW
BEGIN
    DECLARE season_start DATE;
    DECLARE target_weekday INTEGER;
    DECLARE expected_start DATE;

    SELECT s.start_date, FIELD(r.week_start_day, 'MONDAY', 'TUESDAY', 'WEDNESDAY',
                               'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') - 1
    INTO season_start, target_weekday
    FROM seasons s
    JOIN rules r ON r.id = s.rules_id
    WHERE s.id = NEW.season_id;

    IF target_weekday < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Season has an invalid week start day';
    END IF;

    SET expected_start = DATE_ADD(
        season_start,
        INTERVAL (MOD(target_weekday - WEEKDAY(season_start) + 7, 7)
                  + (NEW.week_number - 1) * 7) DAY
    );

    IF NEW.start_date <> expected_start
       OR NEW.end_date <> DATE_ADD(expected_start, INTERVAL 7 DAY) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Week date range does not match the season schedule';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM games
        WHERE week_id = OLD.id
          AND (DATE(game_time) < NEW.start_date OR DATE(game_time) >= NEW.end_date)
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Week date range cannot exclude an existing game';
    END IF;
END$$

CREATE TRIGGER games_week_date_before_insert
BEFORE INSERT ON games
FOR EACH ROW
BEGIN
    DECLARE week_start DATE;
    DECLARE week_end DATE;

    SELECT start_date, end_date INTO week_start, week_end
    FROM weeks
    WHERE id = NEW.week_id;

    IF DATE(NEW.game_time) < week_start OR DATE(NEW.game_time) >= week_end THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Game date must fall within the selected week';
    END IF;
END$$

CREATE TRIGGER games_week_date_before_update
BEFORE UPDATE ON games
FOR EACH ROW
BEGIN
    DECLARE week_start DATE;
    DECLARE week_end DATE;

    SELECT start_date, end_date INTO week_start, week_end
    FROM weeks
    WHERE id = NEW.week_id;

    IF DATE(NEW.game_time) < week_start OR DATE(NEW.game_time) >= week_end THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Game date must fall within the selected week';
    END IF;
END$$

DELIMITER ;
