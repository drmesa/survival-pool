package com.nothingtothetable.survivalpool.service;

import com.nothingtothetable.survivalpool.api.v1.ApiPlayer;
import com.nothingtothetable.survivalpool.types.DomainPlayer;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService
{
    public Optional<ApiPlayer> findById(Long playerId)
    {
        return findDomainPlayerById(playerId).map(this::toApi);
    }

    private Optional<DomainPlayer> findDomainPlayerById(Long playerId)
    {
        return Optional.empty();
    }

    private ApiPlayer toApi(DomainPlayer player)
    {
        ApiPlayer apiPlayer = new ApiPlayer();
        apiPlayer.setId(player.getId());
        apiPlayer.setName(player.getName());
        apiPlayer.setStrikes(player.getStrikes());
        apiPlayer.setCurrentPick(player.getCurrentPick());
        apiPlayer.setPastPicks(player.getPastPicks());
        return apiPlayer;
    }
}
