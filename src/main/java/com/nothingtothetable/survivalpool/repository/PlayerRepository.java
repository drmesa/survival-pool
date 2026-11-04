package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainPlayer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface PlayerRepository
{
    @Select("SELECT id, user_id AS userId, name FROM players WHERE id = #{playerId}")
    DomainPlayer findById(@Param("playerId") Long playerId);
}
