package com.nothingtothetable.survivalpool.controller.v1;

import com.nothingtothetable.survivalpool.types.DomainTeam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/team")
public class TeamController
{
    @GetMapping("/{id}")
    public DomainTeam read(@PathVariable("id") Long teamId)
    {
        DomainTeam team = new DomainTeam();
        team.setName("Baltimore Ravens");

        return team;
    }
}
