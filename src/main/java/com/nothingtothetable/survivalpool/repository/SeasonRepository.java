package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainSeason;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

public interface SeasonRepository
{
    @Select("""
            SELECT id, name, start_date AS startDate, end_date AS endDate,
                   number_of_weeks AS numberOfWeeks, rules_id AS rulesId
            FROM seasons WHERE id = #{seasonId}
            """)
    DomainSeason findById(@Param("seasonId") Long seasonId);

    @Select("""
            SELECT id, name, start_date AS startDate, end_date AS endDate,
                   number_of_weeks AS numberOfWeeks, rules_id AS rulesId
            FROM seasons
            WHERE start_date <= #{date} AND end_date >= #{date}
            ORDER BY start_date DESC
            LIMIT 1
            """)
    DomainSeason findCurrent(@Param("date") LocalDate date);
}
