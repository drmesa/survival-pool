package com.nothingtothetable.survivalpool.controller.v1;

import com.nothingtothetable.survivalpool.mapper.PlayerMapper;
import com.nothingtothetable.survivalpool.service.PlayerPageService;
import com.nothingtothetable.survivalpool.types.PlayerSeasonSummary;
import com.nothingtothetable.survivalpool.types.DomainPick;
import com.nothingtothetable.survivalpool.types.DomainPlayer;
import com.nothingtothetable.survivalpool.types.DomainPlayerPage;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlayerControllerTest
{
    private MockMvc mockMvc;

    private PlayerPageService playerService;

    private PlayerMapper playerMapper;

    @BeforeEach
    void setUp()
    {
        playerService = mock(PlayerPageService.class);
        playerMapper = Mappers.getMapper(PlayerMapper.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new PlayerController(playerService, playerMapper)).build();
    }

    @Test
    void returnsPlayerWhenFound() throws Exception
    {
        DomainPlayer player = new DomainPlayer();
        player.setId(42L);
        player.setName("Alex");
        DomainPick pick = new DomainPick();
        pick.setId(7L);
        pick.setWeekNumber(1);
        pick.setTeamSeasonId(99L);
        pick.setTeamName("Baltimore Ravens");
        pick.setTeamAbbreviation("BAL");
        PlayerSeasonSummary seasonPage = new PlayerSeasonSummary();
        seasonPage.setId(2026L);
        seasonPage.setName("2025-2026");
        seasonPage.setPicks(List.of(pick));
        seasonPage.setStrikes(1);
        seasonPage.setPaidIn(true);
        DomainPlayerPage page = new DomainPlayerPage();
        page.setPlayer(player);
        page.setCurrentSeason(seasonPage);
        when(playerService.findById(42L)).thenReturn(Optional.of(page));

        mockMvc.perform(get("/players/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.player.id").value(42))
                .andExpect(jsonPath("$.player.name").value("Alex"))
                .andExpect(jsonPath("$.currentSeason.id").value(2026))
                .andExpect(jsonPath("$.currentSeason.strikes").value(1))
                .andExpect(jsonPath("$.currentSeason.paidIn").value(true))
                .andExpect(jsonPath("$.currentSeason.picks[0].id").value(7))
                .andExpect(jsonPath("$.currentSeason.picks[0].teamName").value("Baltimore Ravens"));
    }

    @Test
    void returnsNotFoundWhenPlayerDoesNotExist() throws Exception
    {
        when(playerService.findById(42L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/players/42"))
                .andExpect(status().isNotFound());
    }
}
