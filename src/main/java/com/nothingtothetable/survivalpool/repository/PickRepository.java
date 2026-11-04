package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainPick;
import com.nothingtothetable.survivalpool.types.DomainPickSubmission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PickRepository
{
    @Select("""
            SELECT id, player_season_xref_id AS playerSeasonXrefId, week_id AS weekId
            FROM picks
            WHERE player_season_xref_id = #{playerSeasonXrefId} AND week_id = #{weekId}
            """)
    DomainPick findByPlayerSeasonAndWeek(@Param("playerSeasonXrefId") Long playerSeasonXrefId,
                                         @Param("weekId") Long weekId);

    @Insert("""
            INSERT INTO picks (player_season_xref_id, week_id)
            VALUES (#{playerSeasonXrefId}, #{weekId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DomainPick pick);

    @Select("""
            SELECT p.id, participation.id AS playerSeasonXrefId,
                   w.id AS weekId, w.week_number AS weekNumber,
                   ps.team_season_id AS teamSeasonId, ts.name AS teamName,
                   ts.abbreviation AS teamAbbreviation, ps.submitted_at AS submittedAt
            FROM weeks w
            JOIN player_season_xref participation
                ON participation.season_id = w.season_id
               AND participation.id = #{playerSeasonXrefId}
            LEFT JOIN picks p
                ON p.week_id = w.id
               AND p.player_season_xref_id = participation.id
            LEFT JOIN pick_submissions ps ON ps.id = (
                SELECT s.id
                FROM pick_submissions s
                WHERE s.pick_id = p.id
                ORDER BY s.submitted_at DESC, s.id DESC
                LIMIT 1
            )
            LEFT JOIN team_seasons ts ON ts.id = ps.team_season_id
            ORDER BY w.week_number
            """)
    List<DomainPick> findByPlayerSeason(@Param("playerSeasonXrefId") Long playerSeasonXrefId);

    @Select("""
            SELECT s.id, s.pick_id AS pickId, s.team_season_id AS teamSeasonId,
                   s.submitted_at AS submittedAt,
                   s.submitted_by_user_id AS submittedByUserId,
                   s.event_type AS eventType, s.correction_reason AS correctionReason
            FROM pick_submissions s
            JOIN picks p ON p.id = s.pick_id
            WHERE p.player_season_xref_id = #{playerSeasonXrefId}
            ORDER BY s.submitted_at, s.id
            """)
    List<DomainPickSubmission> findSubmissionsByPlayerSeason(
            @Param("playerSeasonXrefId") Long playerSeasonXrefId);
}
