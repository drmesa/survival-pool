package com.nothingtothetable.survivalpool.service;

import com.nothingtothetable.survivalpool.repository.GameRepository;
import com.nothingtothetable.survivalpool.repository.PaymentRepository;
import com.nothingtothetable.survivalpool.repository.PickRepository;
import com.nothingtothetable.survivalpool.repository.PlayerRepository;
import com.nothingtothetable.survivalpool.repository.PlayerSeasonRepository;
import com.nothingtothetable.survivalpool.repository.RulesRepository;
import com.nothingtothetable.survivalpool.repository.SeasonRepository;
import com.nothingtothetable.survivalpool.repository.WeekRepository;
import com.nothingtothetable.survivalpool.types.DomainPick;
import com.nothingtothetable.survivalpool.types.DomainPlayer;
import com.nothingtothetable.survivalpool.types.DomainPlayerSeasonXref;
import com.nothingtothetable.survivalpool.types.DomainRules;
import com.nothingtothetable.survivalpool.types.DomainSeason;
import com.nothingtothetable.survivalpool.types.DomainWeek;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerPageServiceTest
{
    @Test
    void returnsEmptyWhenPlayerDoesNotExist()
    {
        PlayerRepository players = mock(PlayerRepository.class);
        PlayerPageService service = service(players);
        when(players.findById(404L)).thenReturn(null);

        assertTrue(service.findById(404L).isEmpty());
    }

    @Test
    void returnsIdentityWhenNoSeasonIsCurrent()
    {
        PlayerRepository players = mock(PlayerRepository.class);
        SeasonRepository seasons = mock(SeasonRepository.class);
        PlayerPageService service = service(players, seasons);
        DomainPlayer player = new DomainPlayer();
        player.setId(42L);
        player.setName("Alex");
        when(players.findById(42L)).thenReturn(player);
        when(seasons.findCurrent(any())).thenReturn(null);

        var result = service.findById(42L);

        assertTrue(result.isPresent());
        assertEquals(player, result.get().getPlayer());
        assertNull(result.get().getCurrentSeason());
    }

    @Test
    void composesCurrentSeasonAndDerivesPaidInStatus()
    {
        PlayerRepository players = mock(PlayerRepository.class);
        SeasonRepository seasons = mock(SeasonRepository.class);
        PlayerSeasonRepository participations = mock(PlayerSeasonRepository.class);
        RulesRepository rules = mock(RulesRepository.class);
        PaymentRepository payments = mock(PaymentRepository.class);
        WeekRepository weeks = mock(WeekRepository.class);
        GameRepository games = mock(GameRepository.class);
        PickRepository picks = mock(PickRepository.class);

        DomainPlayer player = new DomainPlayer();
        player.setId(42L);
        player.setName("Alex");
        when(players.findById(42L)).thenReturn(player);

        DomainSeason season = new DomainSeason();
        season.setId(2026L);
        season.setName("2025-2026");
        season.setRulesId(3L);
        when(seasons.findCurrent(any())).thenReturn(season);

        DomainPlayerSeasonXref participation = new DomainPlayerSeasonXref();
        participation.setId(10L);
        when(participations.findByPlayerAndSeason(42L, 2026L)).thenReturn(participation);

        DomainRules ruleset = new DomainRules();
        ruleset.setEntryFee(new BigDecimal("25.00"));
        when(rules.findById(3L)).thenReturn(ruleset);
        when(payments.totalForPlayerSeason(10L)).thenReturn(new BigDecimal("30.00"));
        DomainWeek week = new DomainWeek();
        week.setId(5L);
        week.setWeekNumber(1);
        week.setStartDate(LocalDate.of(2026, 11, 5));
        week.setEndDate(LocalDate.of(2026, 11, 12));
        when(weeks.findBySeasonId(2026L)).thenReturn(List.of(week));
        when(games.findBySeasonId(2026L)).thenReturn(List.of());
        DomainPick emptyWeekPick = new DomainPick();
        emptyWeekPick.setPlayerSeasonXrefId(10L);
        emptyWeekPick.setWeekId(5L);
        emptyWeekPick.setWeekNumber(1);
        when(picks.findByPlayerSeason(10L)).thenReturn(List.of(emptyWeekPick));
        when(picks.findSubmissionsByPlayerSeason(10L)).thenReturn(List.of());

        PlayerPageService service = new PlayerPageService(players, seasons, participations,
                rules, payments, weeks, games, picks, new StrikeCalculator());

        var result = service.findById(42L).orElseThrow();

        assertEquals(2026L, result.getCurrentSeason().getId());
        assertEquals(1, result.getCurrentSeason().getPicks().size());
        assertEquals(1, result.getCurrentSeason().getPicks().getFirst().getWeekNumber());
        assertNull(result.getCurrentSeason().getPicks().getFirst().getTeamSeasonId());
        assertEquals(0, result.getCurrentSeason().getStrikes());
        assertTrue(result.getCurrentSeason().getPaidIn());
    }

    @Test
    void leavesPlayerUnpaidWhenPaymentTotalIsBelowEntryFee()
    {
        PlayerRepository players = mock(PlayerRepository.class);
        SeasonRepository seasons = mock(SeasonRepository.class);
        PlayerSeasonRepository participations = mock(PlayerSeasonRepository.class);
        RulesRepository rules = mock(RulesRepository.class);
        PaymentRepository payments = mock(PaymentRepository.class);
        WeekRepository weeks = mock(WeekRepository.class);
        GameRepository games = mock(GameRepository.class);
        PickRepository picks = mock(PickRepository.class);

        DomainPlayer player = new DomainPlayer();
        player.setId(42L);
        when(players.findById(42L)).thenReturn(player);

        DomainSeason season = new DomainSeason();
        season.setId(2026L);
        season.setRulesId(3L);
        when(seasons.findCurrent(any())).thenReturn(season);

        DomainPlayerSeasonXref participation = new DomainPlayerSeasonXref();
        participation.setId(10L);
        when(participations.findByPlayerAndSeason(42L, 2026L)).thenReturn(participation);

        DomainRules ruleset = new DomainRules();
        ruleset.setEntryFee(new BigDecimal("25.00"));
        when(rules.findById(3L)).thenReturn(ruleset);
        when(payments.totalForPlayerSeason(10L)).thenReturn(new BigDecimal("24.99"));
        when(weeks.findBySeasonId(2026L)).thenReturn(List.of());
        when(games.findBySeasonId(2026L)).thenReturn(List.of());
        when(picks.findByPlayerSeason(10L)).thenReturn(List.of());
        when(picks.findSubmissionsByPlayerSeason(10L)).thenReturn(List.of());

        PlayerPageService service = new PlayerPageService(players, seasons, participations,
                rules, payments, weeks, games, picks, new StrikeCalculator());

        var result = service.findById(42L).orElseThrow();

        assertFalse(result.getCurrentSeason().getPaidIn());
    }

    private PlayerPageService service(PlayerRepository players)
    {
        return service(players, mock(SeasonRepository.class));
    }

    private PlayerPageService service(PlayerRepository players, SeasonRepository seasons)
    {
        return new PlayerPageService(
                players,
                seasons,
                mock(PlayerSeasonRepository.class),
                mock(RulesRepository.class),
                mock(PaymentRepository.class),
                mock(WeekRepository.class),
                mock(GameRepository.class),
                mock(PickRepository.class),
                new StrikeCalculator());
    }
}
