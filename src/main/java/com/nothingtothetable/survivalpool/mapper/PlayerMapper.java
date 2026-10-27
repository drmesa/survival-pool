package com.nothingtothetable.survivalpool.mapper;

import com.nothingtothetable.survivalpool.api.v1.Player;
import com.nothingtothetable.survivalpool.types.DomainPlayer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerMapper
{
    Player toPlayer(DomainPlayer player);
}
