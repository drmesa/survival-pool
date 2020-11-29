package types;

import java.util.List;

public class DomainWeek
{
    private Long id;
    private List<DomainGame> games;
    private DomainSeason season;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public List<DomainGame> getGames()
    {
        return games;
    }

    public void setGames(List<DomainGame> games)
    {
        this.games = games;
    }

    public DomainSeason getSeason()
    {
        return season;
    }

    public void setSeason(DomainSeason season)
    {
        this.season = season;
    }
}
