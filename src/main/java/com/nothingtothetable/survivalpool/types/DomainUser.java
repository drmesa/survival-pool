package com.nothingtothetable.survivalpool.types;

public class DomainUser
{
    private Long id;
    private String email;
    private DomainPlayer player;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public DomainPlayer getPlayer()
    {
        return player;
    }

    public void setPlayer(DomainPlayer player)
    {
        this.player = player;
    }
}
