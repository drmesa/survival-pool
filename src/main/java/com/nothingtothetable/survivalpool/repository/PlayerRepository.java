package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainPlayer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface PlayerRepository
{
    @Select("SELECT id, name, strikes FROM players WHERE id = #{playerId}")
    DomainPlayer findById(@Param("playerId") Long playerId);
}
