package com.nothingtothetable.survivalpool.service;

import com.nothingtothetable.survivalpool.repository.GameRepository;
import com.nothingtothetable.survivalpool.repository.PickRepository;
import com.nothingtothetable.survivalpool.repository.PickSubmissionRepository;
import com.nothingtothetable.survivalpool.repository.PlayerRepository;
import com.nothingtothetable.survivalpool.repository.PlayerSeasonRepository;
import com.nothingtothetable.survivalpool.repository.RulesRepository;
import com.nothingtothetable.survivalpool.repository.SeasonRepository;
import com.nothingtothetable.survivalpool.repository.TeamSeasonRepository;
import com.nothingtothetable.survivalpool.repository.WeekRepository;
import com.nothingtothetable.survivalpool.types.DomainGame;
import com.nothingtothetable.survivalpool.types.DomainPick;
import com.nothingtothetable.survivalpool.types.DomainPickSubmission;
import com.nothingtothetable.survivalpool.types.DomainPlayer;
import com.nothingtothetable.survivalpool.types.DomainPlayerSeasonXref;
import com.nothingtothetable.survivalpool.types.DomainRules;
import com.nothingtothetable.survivalpool.types.DomainSeason;
import com.nothingtothetable.survivalpool.types.DomainTeamSeason;
import com.nothingtothetable.survivalpool.types.DomainWeek;
import com.nothingtothetable.survivalpool.types.GameStatus;
import com.nothingtothetable.survivalpool.types.PickSubmissionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PickSubmissionServiceTest
{
    private final PlayerRepository playerRepository = mock(PlayerRepository.class);
    private final PlayerSeasonRepository playerSeasonRepository = mock(PlayerSeasonRepository.class);
    private final SeasonRepository seasonRepository = mock(SeasonRepository.class);
    private final RulesRepository rulesRepository = mock(RulesRepository.class);
    private final WeekRepository weekRepository = mock(WeekRepository.class);
    private final TeamSeasonRepository teamSeasonRepository = mock(TeamSeasonRepository.class);
    private final GameRepository gameRepository = mock(GameRepository.class);
    private final PickRepository pickRepository = mock(PickRepository.class);
    private final PickSubmissionRepository submissionRepository = mock(PickSubmissionRepository.class);
    private final PickSubmissionService service = new PickSubmissionService(
            playerRepository, playerSeasonRepository, seasonRepository, rulesRepository, weekRepository,
            teamSeasonRepository, gameRepository, pickRepository, submissionRepository);

    @BeforeEach
    void setUp()
    {
        DomainPlayerSeasonXref participation = new DomainPlayerSeasonXref();
        participation.setId(100L);
        participation.setPlayerId(42L);
        participation.setSeasonId(2026L);
        when(playerSeasonRepository.findById(100L)).thenReturn(participation);

        DomainWeek week = new DomainWeek();
        week.setId(12L);
        week.setSeasonId(2026L);
        week.setWeekNumber(1);
        week.setStartDate(LocalDate.of(2026, 1, 1));
        week.setEndDate(LocalDate.of(2026, 1, 8));
        when(weekRepository.findById(12L)).thenReturn(week);

        DomainSeason season = new DomainSeason();
        season.setId(2026L);
        season.setStartDate(LocalDate.of(2026, 1, 1));
        season.setRulesId(5L);
        when(seasonRepository.findById(2026L)).thenReturn(season);

        DomainRules rules = new DomainRules();
        rules.setId(5L);
        rules.setWeekStartDay(DayOfWeek.THURSDAY);
        rules.setAllPicksBeforeWeek(false);
        rules.setReuseTeams(true);
        when(rulesRepository.findById(5L)).thenReturn(rules);

        DomainTeamSeason homeTeam = new DomainTeamSeason();
        homeTeam.setId(55L);
        homeTeam.setSeasonId(2026L);
        when(teamSeasonRepository.findById(55L)).thenReturn(homeTeam);

        DomainTeamSeason awayTeam = new DomainTeamSeason();
        awayTeam.setId(66L);
        awayTeam.setSeasonId(2026L);
        when(teamSeasonRepository.findById(66L)).thenReturn(awayTeam);

        when(gameRepository.findByWeekId(12L)).thenReturn(List.of(
                game(55L, 66L, null)));

        DomainPlayer player = new DomainPlayer();
        player.setId(42L);
        player.setUserId(7L);
        when(playerRepository.findById(42L)).thenReturn(player);

        when(submissionRepository.insert(any())).thenReturn(1);
        when(pickRepository.insert(any())).thenAnswer(invocation -> {
            DomainPick pick = invocation.getArgument(0);
            pick.setId(300L);
            return 1;
        });
    }

    @Test
    void recordsNormalSubmissionWithActorAndTimestamp()
    {
        DomainPickSubmission saved = service.submit(100L, 12L, 55L, 7L);

        assertEquals(PickSubmissionType.SUBMISSION, saved.getEventType());
        assertEquals(7L, saved.getSubmittedByUserId());
        ArgumentCaptor<DomainPickSubmission> captor = ArgumentCaptor.forClass(DomainPickSubmission.class);
        verify(submissionRepository).insert(captor.capture());
        assertEquals(55L, captor.getValue().getTeamSeasonId());
        assertEquals(300L, captor.getValue().getPickId());
    }

    @Test
    void rejectsSubmissionByAnotherUsersAccount()
    {
        assertThrows(SecurityException.class, () -> service.submit(100L, 12L, 55L, 8L));
        verify(pickRepository, never()).insert(any());
    }

    @Test
    void locksAllPicksOnceTheFirstGameStarts()
    {
        DomainRules rules = new DomainRules();
        rules.setId(5L);
        rules.setWeekStartDay(DayOfWeek.THURSDAY);
        rules.setAllPicksBeforeWeek(true);
        rules.setReuseTeams(true);
        when(rulesRepository.findById(5L)).thenReturn(rules);
        DomainGame startedGame = game(55L, 66L, Instant.parse("2026-01-01T17:00:00Z"));
        startedGame.setGameTime(Instant.parse("2026-01-01T16:00:00Z"));
        when(gameRepository.findByWeekId(12L)).thenReturn(List.of(startedGame));

        assertThrows(IllegalStateException.class, () -> service.submit(100L, 12L, 55L, 7L));
        verify(submissionRepository, never()).insert(any());
    }

    @Test
    void blocksTeamReuseOnlyAfterItsGameHasStarted()
    {
        DomainRules rules = new DomainRules();
        rules.setId(5L);
        rules.setWeekStartDay(DayOfWeek.THURSDAY);
        rules.setAllPicksBeforeWeek(false);
        rules.setReuseTeams(false);
        when(rulesRepository.findById(5L)).thenReturn(rules);
        when(gameRepository.wasTeamUsed(eq(100L), eq(55L), any(Instant.class))).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.submit(100L, 12L, 55L, 7L));
        verify(submissionRepository, never()).insert(any());
    }

    @Test
    void requiresReasonForCorrection()
    {
        assertThrows(IllegalArgumentException.class,
                () -> service.correct(100L, 12L, 55L, 99L, "   "));
    }

    @Test
    void correctionAppendsHistoryAndRequiresAnExistingSubmission()
    {
        DomainPick pick = new DomainPick();
        pick.setId(300L);
        when(pickRepository.findByPlayerSeasonAndWeek(100L, 12L)).thenReturn(pick);
        DomainPickSubmission original = new DomainPickSubmission();
        original.setPickId(300L);
        original.setId(400L);
        original.setSubmittedAt(Instant.parse("2026-01-01T10:00:00Z"));
        when(pickRepository.findSubmissionsByPlayerSeason(100L)).thenReturn(List.of(original));

        DomainPickSubmission correction = service.correct(100L, 12L, 66L, 99L, "Score correction");

        assertEquals(PickSubmissionType.ADMIN_CORRECTION, correction.getEventType());
        assertEquals("Score correction", correction.getCorrectionReason());
        assertEquals(99L, correction.getSubmittedByUserId());
    }

    @Test
    void correctionFailsWhenPickSlotHasNoHistory()
    {
        DomainPick pick = new DomainPick();
        pick.setId(300L);
        when(pickRepository.findByPlayerSeasonAndWeek(100L, 12L)).thenReturn(pick);
        when(pickRepository.findSubmissionsByPlayerSeason(100L)).thenReturn(List.of());

        assertThrows(NoSuchElementException.class,
                () -> service.correct(100L, 12L, 66L, 99L, "Correction"));
        verify(submissionRepository, never()).insert(any());
    }

    private DomainGame game(Long homeTeamId, Long awayTeamId, Instant actualStartTime)
    {
        DomainGame game = new DomainGame();
        DomainWeek week = new DomainWeek();
        week.setId(12L);
        week.setWeekNumber(1);
        week.setStartDate(LocalDate.of(2026, 1, 1));
        week.setEndDate(LocalDate.of(2026, 1, 8));
        game.setWeek(week);
        game.setHomeTeamSeasonId(homeTeamId);
        game.setAwayTeamSeasonId(awayTeamId);
        game.setGameTime(Instant.now().plusSeconds(86_400));
        game.setActualStartTime(actualStartTime);
        game.setStatus(GameStatus.SCHEDULED);
        return game;
    }
}
