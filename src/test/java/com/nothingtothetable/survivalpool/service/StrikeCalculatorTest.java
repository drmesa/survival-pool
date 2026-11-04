package com.nothingtothetable.survivalpool.service;

import com.nothingtothetable.survivalpool.types.DomainGame;
import com.nothingtothetable.survivalpool.types.DomainPick;
import com.nothingtothetable.survivalpool.types.DomainPickSubmission;
import com.nothingtothetable.survivalpool.types.DomainRules;
import com.nothingtothetable.survivalpool.types.DomainWeek;
import com.nothingtothetable.survivalpool.types.GameStatus;
import com.nothingtothetable.survivalpool.types.PickSubmissionType;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrikeCalculatorTest
{
    private static final Instant AFTER_WEEK = Instant.parse("2026-01-08T12:00:00Z");
    private final StrikeCalculator calculator = new StrikeCalculator();

    @Test
    void countsMissingPickAfterWeekEndsWhenPicksAreNotDueBeforeWeek()
    {
        assertEquals(1, calculate(false, true, List.of(), List.of(), List.of(), AFTER_WEEK));
    }

    @Test
    void usesWeekStartDayEndOfDayAsAllPicksDeadlineWhenNoGameIsScheduledThatDay()
    {
        DomainGame saturdayGame = game(1L, 10L, 20L, "2026-01-03T17:00:00Z",
                null, GameStatus.SCHEDULED, 0, 0);

        assertEquals(1, calculate(true, true, List.of(saturdayGame), List.of(), List.of(),
                Instant.parse("2026-01-02T05:00:00Z")));
        assertEquals(0, calculate(true, true, List.of(saturdayGame), List.of(), List.of(),
                Instant.parse("2026-01-02T04:00:00Z")));
    }

    @Test
    void countsPickMissingAtTheFirstGameStartDeadline()
    {
        DomainGame thursdayGame = game(1L, 10L, 20L, "2026-01-01T17:00:00Z",
                "2026-01-01T17:00:00Z", GameStatus.FINAL, 20, 10);

        assertEquals(1, calculate(true, true, List.of(thursdayGame), List.of(), List.of(),
                Instant.parse("2026-01-01T18:00:00Z")));
    }

    @Test
    void acceptsPickSubmittedBeforeFirstGameStart()
    {
        DomainGame thursdayGame = game(1L, 10L, 20L, "2026-01-01T17:00:00Z",
                "2026-01-01T17:00:00Z", GameStatus.FINAL, 20, 10);

        assertEquals(0, calculate(true, true, List.of(thursdayGame), List.of(pick(1L, 1L, 10L)),
                List.of(submission(1L, "2026-01-01T16:00:00Z")), AFTER_WEEK));
    }

    @Test
    void postponedStartDayGameDoesNotLockBeforeEndOfDayFallback()
    {
        DomainGame postponedGame = game(1L, 10L, 20L, "2026-01-01T17:00:00Z",
                null, GameStatus.POSTPONED, 0, 0);

        assertEquals(0, calculate(true, true, List.of(postponedGame), List.of(), List.of(),
                Instant.parse("2026-01-02T04:00:00Z")));
        assertEquals(1, calculate(true, true, List.of(postponedGame), List.of(), List.of(),
                Instant.parse("2026-01-02T05:00:00Z")));
    }

    @Test
    void appliesTieSafetySettingToFinalTiedGame()
    {
        DomainGame tiedGame = game(1L, 10L, 20L, "2026-01-01T17:00:00Z",
                "2026-01-01T17:00:00Z", GameStatus.FINAL, 14, 14);
        DomainPick pick = pick(1L, 1L, 10L);
        DomainPickSubmission submission = submission(1L, "2026-01-01T16:00:00Z");

        assertEquals(0, calculate(false, true, List.of(tiedGame), List.of(pick),
                List.of(submission), AFTER_WEEK));
        assertEquals(1, calculate(false, false, List.of(tiedGame), List.of(pick),
                List.of(submission), AFTER_WEEK));
    }

    @Test
    void countsLossFromFinalGame()
    {
        DomainGame game = game(1L, 10L, 20L, "2026-01-01T17:00:00Z",
                "2026-01-01T17:00:00Z", GameStatus.FINAL, 14, 21);

        assertEquals(1, calculate(false, true, List.of(game), List.of(pick(1L, 1L, 10L)),
                List.of(submission(1L, "2026-01-01T16:00:00Z")), AFTER_WEEK));
    }

    @Test
    void onlyCountsPicksAndSubmissionsForRequestedPlayerSeason()
    {
        DomainGame game = game(1L, 10L, 20L, "2026-01-01T17:00:00Z",
                "2026-01-01T17:00:00Z", GameStatus.FINAL, 21, 14);
        DomainPick playerPick = pick(1L, 1L, 10L);
        DomainPick otherPlayersPick = pick(2L, 1L, 20L);
        otherPlayersPick.setPlayerSeasonXrefId(200L);

        assertEquals(0, calculate(false, true, List.of(game),
                List.of(playerPick, otherPlayersPick),
                List.of(submission(1L, "2026-01-01T16:00:00Z"),
                        submission(2L, "2026-01-01T16:00:00Z")),
                AFTER_WEEK));
    }

    private int calculate(boolean allPicksBeforeWeek, boolean tiesAreSafe,
                          List<DomainGame> games, List<DomainPick> picks,
                          List<DomainPickSubmission> submissions, Instant now)
    {
        DomainRules rules = new DomainRules();
        rules.setAllPicksBeforeWeek(allPicksBeforeWeek);
        rules.setTiesAreSafe(tiesAreSafe);
        rules.setWeekStartDay(DayOfWeek.THURSDAY);
        DomainWeek week = new DomainWeek();
        week.setId(1L);
        week.setWeekNumber(1);
        week.setStartDate(LocalDate.of(2026, 1, 1));
        week.setEndDate(LocalDate.of(2026, 1, 8));
        return calculator.calculate(100L, rules, List.of(week), games, picks, submissions, now);
    }

    private DomainGame game(Long weekId, Long homeTeamId, Long awayTeamId, String gameTime,
                            String actualStartTime, GameStatus status, int homeScore, int awayScore)
    {
        DomainGame game = new DomainGame();
        DomainWeek week = new DomainWeek();
        week.setId(weekId);
        week.setWeekNumber(1);
        week.setStartDate(LocalDate.of(2026, 1, 1));
        week.setEndDate(LocalDate.of(2026, 1, 8));
        game.setWeek(week);
        game.setHomeTeamSeasonId(homeTeamId);
        game.setAwayTeamSeasonId(awayTeamId);
        game.setGameTime(Instant.parse(gameTime));
        game.setActualStartTime(actualStartTime == null ? null : Instant.parse(actualStartTime));
        game.setStatus(status);
        game.setHomeScore(homeScore);
        game.setAwayScore(awayScore);
        return game;
    }

    private DomainPick pick(Long pickId, Long weekId, Long teamSeasonId)
    {
        DomainPick pick = new DomainPick();
        pick.setId(pickId);
        pick.setPlayerSeasonXrefId(100L);
        pick.setWeekId(weekId);
        pick.setTeamSeasonId(teamSeasonId);
        return pick;
    }

    private DomainPickSubmission submission(Long pickId, String submittedAt)
    {
        DomainPickSubmission submission = new DomainPickSubmission();
        submission.setPickId(pickId);
        submission.setSubmittedAt(Instant.parse(submittedAt));
        submission.setEventType(PickSubmissionType.SUBMISSION);
        return submission;
    }
}
