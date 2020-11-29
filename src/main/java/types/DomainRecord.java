package types;

public class DomainRecord
{
    private Long id;
    private Integer wins;
    private Integer losses;
    private Integer ties;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Integer getWins()
    {
        return wins;
    }

    public void setWins(Integer wins)
    {
        this.wins = wins;
    }

    public Integer getLosses()
    {
        return losses;
    }

    public void setLosses(Integer losses)
    {
        this.losses = losses;
    }

    public Integer getTies()
    {
        return ties;
    }

    public void setTies(Integer ties)
    {
        this.ties = ties;
    }
}
