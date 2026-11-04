package com.nothingtothetable.survivalpool.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.nothingtothetable.survivalpool.types.Franchise;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(DatabaseSchemaTest.TestConfiguration.class)
class DatabaseSchemaTest
{
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void schemaEnforcesPlayerAndPickUniqueness()
    {
        new ResourceDatabasePopulator(new ClassPathResource("db/schema.sql")).execute(dataSource);
        new ResourceDatabasePopulator(new ClassPathResource("db/seed-franchises.sql")).execute(dataSource);
        for (Franchise franchise : Franchise.values())
        {
            String seededCode = jdbcTemplate.queryForObject(
                    "SELECT code FROM franchises WHERE id = ?", String.class, franchise.getId());
            assertEquals(franchise.getCode(), seededCode);
        }
        jdbcTemplate.update("INSERT INTO users (email) VALUES (?)", "alex@example.com");
        jdbcTemplate.update("""
                INSERT INTO rules (rule_set_name, entry_fee, strikes_allowed, all_picks_before_week,
                    week_start_day, reuse_teams, ties_are_safe, payout_split)
                VALUES ('Default', 25.00, 2, FALSE, 'THURSDAY', FALSE, TRUE, 'WINNER_TAKE_ALL')
                """);
        jdbcTemplate.update("""
                INSERT INTO seasons (name, start_date, end_date, number_of_weeks, rules_id)
                VALUES ('2025-2026', '2025-09-04', '2026-01-08', 18, 1)
                """);
        jdbcTemplate.update("INSERT INTO players (user_id, name) VALUES (1, 'Alex')");
        jdbcTemplate.update("INSERT INTO player_season_xref (player_id, season_id) VALUES (1, 1)");
        jdbcTemplate.update("""
                INSERT INTO team_seasons (franchise_id, season_id, name, abbreviation)
                VALUES (?, 1, 'Baltimore Ravens', 'BAL')
                """, Franchise.RAVENS_1996.getId());
        jdbcTemplate.update("""
                INSERT INTO team_seasons (franchise_id, season_id, name, abbreviation)
                VALUES (?, 1, 'Cleveland Browns', 'CLE')
                """, Franchise.BROWNS_1946.getId());
        jdbcTemplate.update("""
                INSERT INTO weeks (season_id, week_number, start_date, end_date)
                VALUES (1, 1, '2025-09-04', '2025-09-11')
                """);
        jdbcTemplate.update("""
                INSERT INTO games (week_id, home_team_season_id, away_team_season_id, game_time, game_status)
                VALUES (1, 1, 2, '2025-09-04 17:00:00', 'SCHEDULED')
                """);
        jdbcTemplate.update("INSERT INTO picks (player_season_xref_id, week_id) VALUES (1, 1)");

        assertThrows(DataIntegrityViolationException.class,
                () -> jdbcTemplate.update("INSERT INTO players (user_id, name) VALUES (1, 'Duplicate')"));
        assertThrows(DataIntegrityViolationException.class,
                () -> jdbcTemplate.update("INSERT INTO picks (player_season_xref_id, week_id) VALUES (1, 1)"));
        assertThrows(DataIntegrityViolationException.class,
                () -> jdbcTemplate.update("""
                        INSERT INTO weeks (season_id, week_number, start_date, end_date)
                        VALUES (1, 2, '2025-09-11', '2025-09-17')
                        """));
    }

    @Configuration
    static class TestConfiguration
    {
        @Bean
        DataSource dataSource()
        {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
            dataSource.setUsername("sa");
            dataSource.setPassword("");
            return dataSource;
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource)
        {
            return new JdbcTemplate(dataSource);
        }
    }
}
