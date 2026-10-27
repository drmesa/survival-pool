package com.nothingtothetable.survivalpool.service;

import com.nothingtothetable.survivalpool.types.DomainPlayer;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService
{
    public Optional<DomainPlayer> findById(Long playerId)
    {
        return Optional.empty();
    }
}
