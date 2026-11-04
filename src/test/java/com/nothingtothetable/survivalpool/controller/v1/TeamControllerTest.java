package com.nothingtothetable.survivalpool.controller.v1;

import com.nothingtothetable.survivalpool.mapper.TeamSeasonMapper;
import com.nothingtothetable.survivalpool.service.TeamSeasonService;
import com.nothingtothetable.survivalpool.types.DomainTeamSeason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TeamControllerTest
{
    private MockMvc mockMvc;
    private TeamSeasonService teamSeasonService;

    @BeforeEach
    void setUp()
    {
        teamSeasonService = mock(TeamSeasonService.class);
        TeamSeasonMapper mapper = Mappers.getMapper(TeamSeasonMapper.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new TeamController(teamSeasonService, mapper)).build();
    }

    @Test
    void returnsMappedTeamSeasonWhenFound() throws Exception
    {
        DomainTeamSeason teamSeason = new DomainTeamSeason();
        teamSeason.setId(5L);
        teamSeason.setFranchiseId(12L);
        teamSeason.setSeasonId(2026L);
        teamSeason.setName("Baltimore Ravens");
        teamSeason.setAbbreviation("BAL");
        when(teamSeasonService.findById(5L)).thenReturn(Optional.of(teamSeason));

        mockMvc.perform(get("/teams/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.franchiseId").value(12))
                .andExpect(jsonPath("$.seasonId").value(2026))
                .andExpect(jsonPath("$.name").value("Baltimore Ravens"))
                .andExpect(jsonPath("$.abbreviation").value("BAL"));
    }

    @Test
    void returnsNotFoundWhenTeamSeasonDoesNotExist() throws Exception
    {
        when(teamSeasonService.findById(404L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/teams/404"))
                .andExpect(status().isNotFound());
    }
}
