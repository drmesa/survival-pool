package com.nothingtothetable.survivalpool.mapper;

import com.nothingtothetable.survivalpool.api.v1.TeamSeason;
import com.nothingtothetable.survivalpool.types.DomainTeamSeason;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamSeasonMapper
{
    TeamSeason toTeamSeason(DomainTeamSeason teamSeason);
}
