package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainPlayerSeasonXref;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface PlayerSeasonRepository
{
    @Select("""
            SELECT id, player_id AS playerId, season_id AS seasonId
            FROM player_season_xref WHERE id = #{id}
            """)
    DomainPlayerSeasonXref findById(@Param("id") Long id);

    @Select("""
            SELECT id, player_id AS playerId, season_id AS seasonId
            FROM player_season_xref
            WHERE player_id = #{playerId} AND season_id = #{seasonId}
            """)
    DomainPlayerSeasonXref findByPlayerAndSeason(@Param("playerId") Long playerId,
                                                  @Param("seasonId") Long seasonId);
}
