package com.nothingtothetable.survivalpool.types;

public class DomainTeam
{
    private Long id;
    private String name;
    private String shortName;
    private DomainSeason season;
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
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
