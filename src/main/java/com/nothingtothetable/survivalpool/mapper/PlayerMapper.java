package com.nothingtothetable.survivalpool.mapper;

import com.nothingtothetable.survivalpool.api.v1.Player;
import com.nothingtothetable.survivalpool.api.v1.PlayerPage;
import com.nothingtothetable.survivalpool.types.PlayerSeasonSummary;
import com.nothingtothetable.survivalpool.types.DomainPick;
import com.nothingtothetable.survivalpool.types.DomainPlayer;
import com.nothingtothetable.survivalpool.types.DomainPlayerPage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlayerMapper
{
    Player toPlayer(DomainPlayer player);

    @Mapping(target = "player", source = "player")
    @Mapping(target = "currentSeason", source = "currentSeason")
    PlayerPage toPlayerPage(DomainPlayerPage page);

    PlayerPage.CurrentSeason toCurrentSeason(PlayerSeasonSummary currentSeason);

    @Mapping(target = "id", source = "id")
    PlayerPage.Pick toPick(DomainPick pick);
}
