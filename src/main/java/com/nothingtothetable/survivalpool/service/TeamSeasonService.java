package com.nothingtothetable.survivalpool.service;

import com.nothingtothetable.survivalpool.repository.TeamSeasonRepository;
import com.nothingtothetable.survivalpool.types.DomainTeamSeason;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamSeasonService
{
    private final TeamSeasonRepository teamSeasonRepository;

    public TeamSeasonService(TeamSeasonRepository teamSeasonRepository)
    {
        this.teamSeasonRepository = teamSeasonRepository;
    }

    public Optional<DomainTeamSeason> findById(Long teamSeasonId)
    {
        return Optional.ofNullable(teamSeasonRepository.findById(teamSeasonId));
    }
}
