package com.nothingtothetable.survivalpool.controller.v1;

import com.nothingtothetable.survivalpool.api.v1.ApiPlayer;
import com.nothingtothetable.survivalpool.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlayerControllerTest
{
    private MockMvc mockMvc;

    private PlayerService playerService;

    @BeforeEach
    void setUp()
    {
        playerService = mock(PlayerService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new PlayerController(playerService)).build();
    }

    @Test
    void returnsPlayerWhenFound() throws Exception
    {
        ApiPlayer player = new ApiPlayer();
        player.setId(42L);
        player.setName("Alex");
        player.setStrikes(1);
        player.setPastPicks(Collections.emptyList());
        when(playerService.findById(42L)).thenReturn(Optional.of(player));

        mockMvc.perform(get("/players/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.strikes").value(1))
                .andExpect(jsonPath("$.pastPicks").isArray());
    }

    @Test
    void returnsNotFoundWhenPlayerDoesNotExist() throws Exception
    {
        when(playerService.findById(42L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/players/42"))
                .andExpect(status().isNotFound());
    }
}
