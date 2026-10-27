package com.nothingtothetable.survivalpool.controller.v1;

import com.nothingtothetable.survivalpool.mapper.PlayerMapper;
import com.nothingtothetable.survivalpool.service.PlayerService;
import com.nothingtothetable.survivalpool.types.DomainPick;
import com.nothingtothetable.survivalpool.types.DomainPlayer;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private PlayerMapper playerMapper;

    @BeforeEach
    void setUp()
    {
        playerService = mock(PlayerService.class);
        playerMapper = Mappers.getMapper(PlayerMapper.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new PlayerController(playerService, playerMapper)).build();
    }

    @Test
    void returnsPlayerWhenFound() throws Exception
    {
        DomainPlayer player = new DomainPlayer();
        player.setId(42L);
        player.setName("Alex");
        player.setStrikes(1);
        DomainPick pastPick = new DomainPick();
        pastPick.setId(7L);
        Map<Long, List<DomainPick>> pastPicks =
                Collections.singletonMap(2026L, Collections.singletonList(pastPick));
        player.setPastPicks(pastPicks);
        when(playerService.findById(42L)).thenReturn(Optional.of(player));

        mockMvc.perform(get("/players/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.strikes").value(1))
                .andExpect(jsonPath("$.pastPicks").isMap())
                .andExpect(jsonPath("$.pastPicks['2026'][0].id").value(7));
    }

    @Test
    void returnsNotFoundWhenPlayerDoesNotExist() throws Exception
    {
        when(playerService.findById(42L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/players/42"))
                .andExpect(status().isNotFound());
    }
}
