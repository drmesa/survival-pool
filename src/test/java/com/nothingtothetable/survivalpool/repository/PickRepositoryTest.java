package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainPick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@MybatisTest
@ContextConfiguration(classes = PickRepositoryTest.MyBatisConfiguration.class)
class PickRepositoryTest
{
    @Autowired
    private PickRepository pickRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Creates the minimal season, week, pick, and submission records needed by repository tests.
     */
    @BeforeEach
    void setUp()
    {
        jdbcTemplate.execute("DROP TABLE IF EXISTS pick_submissions");
        jdbcTemplate.execute("DROP TABLE IF EXISTS team_seasons");
        jdbcTemplate.execute("DROP TABLE IF EXISTS picks");
        jdbcTemplate.execute("DROP TABLE IF EXISTS weeks");
        jdbcTemplate.execute("DROP TABLE IF EXISTS player_season_xref");
        jdbcTemplate.execute("""
                CREATE TABLE player_season_xref (
                    id BIGINT PRIMARY KEY,
                    season_id BIGINT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE weeks (
                    id BIGINT PRIMARY KEY,
                    season_id BIGINT NOT NULL,
                    week_number INTEGER NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE picks (
                    id BIGINT PRIMARY KEY,
                    player_season_xref_id BIGINT NOT NULL,
                    week_id BIGINT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE team_seasons (
                    id BIGINT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    abbreviation VARCHAR(32) NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE pick_submissions (
                    id BIGINT PRIMARY KEY,
                    pick_id BIGINT NOT NULL,
                    team_season_id BIGINT NOT NULL,
                    submitted_at TIMESTAMP NOT NULL
                )
                """);
        jdbcTemplate.update("INSERT INTO player_season_xref (id, season_id) VALUES (10, 20)");
        jdbcTemplate.update("INSERT INTO weeks (id, season_id, week_number) VALUES (100, 20, 1)");
        jdbcTemplate.update("INSERT INTO weeks (id, season_id, week_number) VALUES (101, 20, 2)");
        jdbcTemplate.update("INSERT INTO picks (id, player_season_xref_id, week_id) VALUES (200, 10, 100)");
        jdbcTemplate.update("INSERT INTO team_seasons (id, name, abbreviation) VALUES (300, 'Ravens', 'BAL')");
        jdbcTemplate.update("INSERT INTO team_seasons (id, name, abbreviation) VALUES (301, 'Browns', 'CLE')");
        jdbcTemplate.update("""
                INSERT INTO pick_submissions (id, pick_id, team_season_id, submitted_at)
                VALUES (400, 200, 300, '2026-09-01 10:00:00')
                """);
        jdbcTemplate.update("""
                INSERT INTO pick_submissions (id, pick_id, team_season_id, submitted_at)
                VALUES (401, 200, 301, '2026-09-02 10:00:00')
                """);
    }

    /**
     * Verifies the week-driven query includes empty weeks and returns each existing pick's latest selection.
     */
    @Test
    void returnsEveryWeekWithOnlyTheEffectivePick()
    {
        List<DomainPick> picks = pickRepository.findByPlayerSeason(10L);

        assertEquals(2, picks.size());
        assertEquals(1, picks.get(0).getWeekNumber());
        assertEquals(301L, picks.get(0).getTeamSeasonId());
        assertEquals("Browns", picks.get(0).getTeamName());
        assertEquals(2, picks.get(1).getWeekNumber());
        assertNull(picks.get(1).getId());
        assertNull(picks.get(1).getTeamSeasonId());
    }

    @Configuration
    @MapperScan("com.nothingtothetable.survivalpool.repository")
    static class MyBatisConfiguration
    {
    }
}
