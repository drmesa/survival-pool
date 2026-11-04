package com.nothingtothetable.survivalpool.controller.v1;

import com.nothingtothetable.survivalpool.api.v1.TeamSeason;
import com.nothingtothetable.survivalpool.mapper.TeamSeasonMapper;
import com.nothingtothetable.survivalpool.service.TeamSeasonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams")
public class TeamController
{
    private final TeamSeasonService teamSeasonService;
    private final TeamSeasonMapper teamSeasonMapper;

    public TeamController(TeamSeasonService teamSeasonService, TeamSeasonMapper teamSeasonMapper)
    {
        this.teamSeasonService = teamSeasonService;
        this.teamSeasonMapper = teamSeasonMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamSeason> read(@PathVariable("id") Long teamSeasonId)
    {
        return teamSeasonService.findById(teamSeasonId)
                .map(teamSeasonMapper::toTeamSeason)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
