package com.nothingtothetable.survivalpool.service;

import com.nothingtothetable.survivalpool.repository.PlayerRepository;
import com.nothingtothetable.survivalpool.types.DomainPlayer;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerServiceTest
{
    @Test
    void returnsPlayerWhenRepositoryFindsIt()
    {
        PlayerRepository repository = mock(PlayerRepository.class);
        PlayerService service = new PlayerService(repository);
        DomainPlayer player = new DomainPlayer();
        player.setId(42L);
        player.setName("Alex");
        when(repository.findById(42L)).thenReturn(player);

        assertEquals(Optional.of(player), service.findById(42L));
    }

    @Test
    void returnsEmptyWhenRepositoryDoesNotFindPlayer()
    {
        PlayerRepository repository = mock(PlayerRepository.class);
        PlayerService service = new PlayerService(repository);
        when(repository.findById(404L)).thenReturn(null);

        assertTrue(service.findById(404L).isEmpty());
    }
}
