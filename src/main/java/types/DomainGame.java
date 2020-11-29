package types;

import java.util.Date;

public class DomainGame
{
    private Long id;
    private DomainTeam homeTeam;
    private DomainTeam awayTeam;
    private Date gameTime;
    private DomainWeek week;
    private GameOutcome gameOutcome;
    private Integer homeScore;
    private Integer awayScore;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public DomainTeam getHomeTeam()
    {
        return homeTeam;
    }

    public void setHomeTeam(DomainTeam homeTeam)
    {
        this.homeTeam = homeTeam;
    }

    public DomainTeam getAwayTeam()
    {
        return awayTeam;
    }

    public void setAwayTeam(DomainTeam awayTeam)
    {
        this.awayTeam = awayTeam;
    }

    public Date getGameTime()
    {
        return gameTime;
    }

    public void setGameTime(Date gameTime)
    {
        this.gameTime = gameTime;
    }

    public DomainWeek getWeek()
    {
        return week;
    }

    public void setWeek(DomainWeek week)
    {
        this.week = week;
    }

    public GameOutcome getOutcome()
    {
        return gameOutcome;
    }

    public void setOutcome(GameOutcome gameOutcome)
    {
        this.gameOutcome = gameOutcome;
    }

    public Integer getHomeScore()
    {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore)
    {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore()
    {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore)
    {
        this.awayScore = awayScore;
    }
}
