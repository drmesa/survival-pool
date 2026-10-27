package com.nothingtothetable.survivalpool.controller.v1;

import com.nothingtothetable.survivalpool.api.v1.ApiPlayer;
import com.nothingtothetable.survivalpool.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
public class PlayerController
{
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService)
    {
        this.playerService = playerService;
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<ApiPlayer> read(@PathVariable("playerId") Long playerId)
    {
        return playerService.findById(playerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
