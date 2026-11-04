package com.nothingtothetable.survivalpool.controller.v1;

import com.nothingtothetable.survivalpool.api.v1.PlayerPage;
import com.nothingtothetable.survivalpool.mapper.PlayerMapper;
import com.nothingtothetable.survivalpool.service.PlayerPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
public class PlayerController
{
    private final PlayerPageService playerService;
    private final PlayerMapper playerMapper;

    public PlayerController(PlayerPageService playerService, PlayerMapper playerMapper)
    {
        this.playerService = playerService;
        this.playerMapper = playerMapper;
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerPage> read(@PathVariable("playerId") Long playerId)
    {
        return playerService.findById(playerId)
                .map(playerMapper::toPlayerPage)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
