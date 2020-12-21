package com.nothingtothetable.survivalpool.types;

import java.time.DayOfWeek;

public class DomainRules
{
    private Long id;
    private String ruleSetName;
    private Integer strikesAllowed;
    private Boolean allPicksBeforeWeek;
    private DayOfWeek weekStartDay;
    private Integer numberOfWeeks;
    private CashSplit payout;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getRuleSetName()
    {
        return ruleSetName;
    }

    public void setRuleSetName(String ruleSetName)
    {
        this.ruleSetName = ruleSetName;
    }

    public Integer getStrikesAllowed()
    {
        return strikesAllowed;
    }

    public void setStrikesAllowed(Integer strikesAllowed)
    {
        this.strikesAllowed = strikesAllowed;
    }

    public Boolean getAllPicksBeforeWeek()
    {
        return allPicksBeforeWeek;
    }

    public void setAllPicksBeforeWeek(Boolean allPicksBeforeWeek)
    {
        this.allPicksBeforeWeek = allPicksBeforeWeek;
    }

    public DayOfWeek getWeekStartDay()
    {
        return weekStartDay;
    }

    public void setWeekStartDay(DayOfWeek weekStartDay)
    {
        this.weekStartDay = weekStartDay;
    }

    public Integer getNumberOfWeeks()
    {
        return numberOfWeeks;
    }

    public void setNumberOfWeeks(Integer numberOfWeeks)
    {
        this.numberOfWeeks = numberOfWeeks;
    }

    public CashSplit getPayout()
    {
        return payout;
    }

    public void setPayout(CashSplit payout)
    {
        this.payout = payout;
    }
}
