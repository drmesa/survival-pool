package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainGame;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GameRepository
{
    @Select("""
            SELECT g.id, w.id AS weekId, w.season_id AS weekSeasonId,
                   w.week_number AS weekNumber, w.start_date AS weekStartDate,
                   w.end_date AS weekEndDate,
                   g.home_team_season_id AS homeTeamSeasonId,
                   g.away_team_season_id AS awayTeamSeasonId,
                   g.game_time AS gameTime, g.actual_start_time AS actualStartTime,
                   g.home_score AS homeScore, g.away_score AS awayScore,
                   g.game_status AS status
            FROM games g
            JOIN weeks w ON w.id = g.week_id
            WHERE g.week_id = #{weekId}
            """)
    @Results({
            @Result(property = "id", column = "id", id = true),
            @Result(property = "week.id", column = "weekId"),
            @Result(property = "week.seasonId", column = "weekSeasonId"),
            @Result(property = "week.weekNumber", column = "weekNumber"),
            @Result(property = "week.startDate", column = "weekStartDate"),
            @Result(property = "week.endDate", column = "weekEndDate"),
            @Result(property = "homeTeamSeasonId", column = "homeTeamSeasonId"),
            @Result(property = "awayTeamSeasonId", column = "awayTeamSeasonId"),
            @Result(property = "gameTime", column = "gameTime"),
            @Result(property = "actualStartTime", column = "actualStartTime"),
            @Result(property = "homeScore", column = "homeScore"),
            @Result(property = "awayScore", column = "awayScore"),
            @Result(property = "status", column = "status")
    })
    List<DomainGame> findByWeekId(@Param("weekId") Long weekId);

    @Select("""
            SELECT COUNT(*) > 0
            FROM pick_submissions s
            JOIN picks p ON p.id = s.pick_id
            JOIN games g ON g.week_id = p.week_id
            WHERE p.player_season_xref_id = #{playerSeasonXrefId}
              AND s.team_season_id = #{teamSeasonId}
              AND g.actual_start_time IS NOT NULL
              AND g.actual_start_time <= #{now}
              AND s.submitted_at < g.actual_start_time
              AND (g.home_team_season_id = s.team_season_id
                   OR g.away_team_season_id = s.team_season_id)
            """)
    boolean wasTeamUsed(@Param("playerSeasonXrefId") Long playerSeasonXrefId,
                        @Param("teamSeasonId") Long teamSeasonId,
                        @Param("now") java.time.Instant now);

    @Select("""
            SELECT g.id, w.id AS weekId, w.season_id AS weekSeasonId,
                   w.week_number AS weekNumber, w.start_date AS weekStartDate,
                   w.end_date AS weekEndDate,
                   g.home_team_season_id AS homeTeamSeasonId,
                   g.away_team_season_id AS awayTeamSeasonId,
                   g.game_time AS gameTime, g.actual_start_time AS actualStartTime,
                   g.home_score AS homeScore, g.away_score AS awayScore,
                   g.game_status AS status
            FROM games g
            JOIN weeks w ON w.id = g.week_id
            WHERE w.season_id = #{seasonId}
            ORDER BY g.game_time
            """)
    @Results({
            @Result(property = "id", column = "id", id = true),
            @Result(property = "week.id", column = "weekId"),
            @Result(property = "week.seasonId", column = "weekSeasonId"),
            @Result(property = "week.weekNumber", column = "weekNumber"),
            @Result(property = "week.startDate", column = "weekStartDate"),
            @Result(property = "week.endDate", column = "weekEndDate"),
            @Result(property = "homeTeamSeasonId", column = "homeTeamSeasonId"),
            @Result(property = "awayTeamSeasonId", column = "awayTeamSeasonId"),
            @Result(property = "gameTime", column = "gameTime"),
            @Result(property = "actualStartTime", column = "actualStartTime"),
            @Result(property = "homeScore", column = "homeScore"),
            @Result(property = "awayScore", column = "awayScore"),
            @Result(property = "status", column = "status")
    })
    List<DomainGame> findBySeasonId(@Param("seasonId") Long seasonId);
}
