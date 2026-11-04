package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.DayOfWeek;

@Data
@Accessors(chain = true)
public class DomainRules
{
    private Long id;
    private String ruleSetName;
    private BigDecimal entryFee;
    private Integer strikesAllowed;
    private Boolean allPicksBeforeWeek;
    private DayOfWeek weekStartDay;
    private Boolean reuseTeams;
    private Boolean tiesAreSafe;
    private CashSplit payout;
}
