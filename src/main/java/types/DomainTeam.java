package types;

public class DomainTeam
{
    private Long id;
    private DomainRecord seasonRecord;
    private DomainWeek byeWeek;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public DomainRecord getSeasonRecord()
    {
        return seasonRecord;
    }

    public void setSeasonRecord(DomainRecord seasonRecord)
    {
        this.seasonRecord = seasonRecord;
    }

    public DomainWeek getByeWeek()
    {
        return byeWeek;
    }

    public void setByeWeek(DomainWeek byeWeek)
    {
        this.byeWeek = byeWeek;
    }
}
