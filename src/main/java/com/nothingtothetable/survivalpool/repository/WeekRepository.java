package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainWeek;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface WeekRepository
{
    @Select("""
            SELECT id, season_id AS seasonId, week_number AS weekNumber,
                   start_date AS startDate, end_date AS endDate
            FROM weeks WHERE id = #{weekId}
            """)
    DomainWeek findById(@Param("weekId") Long weekId);

    @Select("""
            SELECT id, season_id AS seasonId, week_number AS weekNumber,
                   start_date AS startDate, end_date AS endDate
            FROM weeks WHERE season_id = #{seasonId}
            ORDER BY week_number
            """)
    List<DomainWeek> findBySeasonId(@Param("seasonId") Long seasonId);
}
