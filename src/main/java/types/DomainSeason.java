package types;

import java.util.List;

public class DomainSeason
{
    private Long id;
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

    public List<DomainWeek> getSchedule()
    {
        return schedule;
    }

    public void setSchedule(List<DomainWeek> schedule)
    {
        this.schedule = schedule;
    }
}
