package com.nothingtothetable.survivalpool.repository;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

public interface PaymentRepository
{
    @Select("""
            SELECT COALESCE(SUM(amount), 0)
            FROM payments
            WHERE player_season_xref_id = #{playerSeasonXrefId}
            """)
    BigDecimal totalForPlayerSeason(@Param("playerSeasonXrefId") Long playerSeasonXrefId);
}
