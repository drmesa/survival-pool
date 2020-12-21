package com.nothingtothetable.survivalpool.types;

import java.util.List;

public class DomainSeason
{
    private Long id;
    private String name;
    private List<DomainWeek> schedule;
    private DomainRules rules;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<DomainWeek> getSchedule()
    {
        return schedule;
    }

    public void setSchedule(List<DomainWeek> schedule)
    {
        this.schedule = schedule;
    }

    public DomainRules getRules()
    {
        return rules;
    }

    public void setRules(DomainRules rules)
    {
        this.rules = rules;
    }
}
