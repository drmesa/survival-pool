package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainGame;
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

@MybatisTest
@ContextConfiguration(classes = GameRepositoryTest.MyBatisConfiguration.class)
class GameRepositoryTest
{
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp()
    {
        jdbcTemplate.execute("DROP TABLE IF EXISTS games");
        jdbcTemplate.execute("DROP TABLE IF EXISTS weeks");
        jdbcTemplate.execute("""
                CREATE TABLE weeks (
                    id BIGINT PRIMARY KEY,
                    season_id BIGINT NOT NULL,
                    week_number INTEGER NOT NULL,
                    start_date DATE NOT NULL,
                    end_date DATE NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE games (
                    id BIGINT PRIMARY KEY,
                    week_id BIGINT NOT NULL,
                    home_team_season_id BIGINT NOT NULL,
                    away_team_season_id BIGINT NOT NULL,
                    game_time TIMESTAMP NOT NULL,
                    actual_start_time TIMESTAMP,
                    home_score INTEGER NOT NULL,
                    away_score INTEGER NOT NULL,
                    game_status VARCHAR(24) NOT NULL
                )
                """);
        jdbcTemplate.update("""
                INSERT INTO weeks (id, season_id, week_number, start_date, end_date)
                VALUES (10, 20, 3, '2026-01-15', '2026-01-22')
                """);
        jdbcTemplate.update("""
                INSERT INTO games (id, week_id, home_team_season_id, away_team_season_id,
                    game_time, actual_start_time, home_score, away_score, game_status)
                VALUES (30, 10, 40, 50, '2026-01-15 17:00:00', NULL, 0, 0, 'SCHEDULED')
                """);
    }

    @Test
    void mapsWeekRelationshipWhenFindingGamesByWeek()
    {
        DomainGame game = gameRepository.findByWeekId(10L).getFirst();

        assertEquals(10L, game.getWeek().getId());
        assertEquals(20L, game.getWeek().getSeasonId());
        assertEquals(3, game.getWeek().getWeekNumber());
        assertEquals(java.time.LocalDate.of(2026, 1, 15), game.getWeek().getStartDate());
        assertEquals(java.time.LocalDate.of(2026, 1, 22), game.getWeek().getEndDate());
    }

    @Test
    void mapsWeekRelationshipWhenFindingGamesBySeason()
    {
        List<DomainGame> games = gameRepository.findBySeasonId(20L);

        assertEquals(10L, games.getFirst().getWeek().getId());
        assertEquals(20L, games.getFirst().getWeek().getSeasonId());
        assertEquals(3, games.getFirst().getWeek().getWeekNumber());
        assertEquals(java.time.LocalDate.of(2026, 1, 15), games.getFirst().getWeek().getStartDate());
        assertEquals(java.time.LocalDate.of(2026, 1, 22), games.getFirst().getWeek().getEndDate());
    }

    @Configuration
    @MapperScan("com.nothingtothetable.survivalpool.repository")
    static class MyBatisConfiguration
    {
    }
}
