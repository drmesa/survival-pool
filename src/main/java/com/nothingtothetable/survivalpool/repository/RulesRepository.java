package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainRules;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RulesRepository
{
    @Select("""
            SELECT id, rule_set_name AS ruleSetName, entry_fee AS entryFee,
                   strikes_allowed AS strikesAllowed,
                   all_picks_before_week AS allPicksBeforeWeek,
                   week_start_day AS weekStartDay, reuse_teams AS reuseTeams,
                   ties_are_safe AS tiesAreSafe, payout_split AS payout
            FROM rules WHERE id = #{rulesId}
            """)
    DomainRules findById(@Param("rulesId") Long rulesId);
}
