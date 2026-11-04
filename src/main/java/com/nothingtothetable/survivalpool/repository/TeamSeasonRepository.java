package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainTeamSeason;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TeamSeasonRepository
{
    @Select("""
            SELECT id, franchise_id AS franchiseId, season_id AS seasonId,
                   name, abbreviation
            FROM team_seasons WHERE id = #{id}
            """)
    DomainTeamSeason findById(@Param("id") Long id);
}
