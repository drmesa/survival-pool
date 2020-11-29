package types;

public class DomainPick
{
    private Long id;
    private DomainWeek week;
    private PickOutcome pickOutcome;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public DomainWeek getWeek()
    {
        return week;
    }

    public void setWeek(DomainWeek week)
    {
        this.week = week;
    }

    public PickOutcome getPickOutcome()
    {
        return pickOutcome;
    }

    public void setPickOutcome(PickOutcome pickOutcome)
    {
        this.pickOutcome = pickOutcome;
    }
}
