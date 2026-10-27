package com.nothingtothetable.survivalpool.service;

import com.nothingtothetable.survivalpool.repository.PlayerRepository;
import com.nothingtothetable.survivalpool.types.DomainPlayer;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService
{
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository)
    {
        this.playerRepository = playerRepository;
    }

    public Optional<DomainPlayer> findById(Long playerId)
    {
        return Optional.ofNullable(playerRepository.findById(playerId));
    }
}
