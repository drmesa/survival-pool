package com.nothingtothetable.survivalpool.service;

import com.nothingtothetable.survivalpool.types.DomainGame;
import com.nothingtothetable.survivalpool.types.DomainPick;
import com.nothingtothetable.survivalpool.types.DomainPickSubmission;
import com.nothingtothetable.survivalpool.types.DomainRules;
import com.nothingtothetable.survivalpool.types.DomainWeek;
import com.nothingtothetable.survivalpool.types.GameOutcome;
import com.nothingtothetable.survivalpool.types.GameStatus;
import com.nothingtothetable.survivalpool.types.PickOutcome;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StrikeCalculator
{
    private static final ZoneId EASTERN = ZoneId.of("America/New_York");

    /**
     * Calculates the player's accumulated strikes from the supplied picks, games, and week date ranges.
     *
     * @param rules season rules governing deadlines and ties
     * @param weeks weeks included in the calculation
     * @param games games played in those weeks
     * @param playerSeasonXrefId the participation whose strikes are being calculated
     * @param picks candidate picks; only picks belonging to the specified participation are considered
     * @param submissions candidate submission history; only submissions for those picks are considered
     * @param now the instant at which to evaluate unfinished weeks
     * @return the number of strikes accumulated
     * @throws IllegalArgumentException if {@code playerSeasonXrefId} is {@code null}
     */
    public int calculate(Long playerSeasonXrefId, DomainRules rules, List<DomainWeek> weeks,
                         List<DomainGame> games, List<DomainPick> picks,
                         List<DomainPickSubmission> submissions, Instant now)
    {
        if (playerSeasonXrefId == null)
        {
            throw new IllegalArgumentException("Player-season participation ID is required");
        }

        List<DomainPick> playerPicks = picks.stream()
                .filter(pick -> Objects.equals(playerSeasonXrefId, pick.getPlayerSeasonXrefId()))
                .toList();
        Map<Long, DomainPick> picksByWeek = playerPicks.stream()
                .filter(pick -> pick.getWeekId() != null)
                .collect(Collectors.toMap(DomainPick::getWeekId, Function.identity()));
        Set<Long> playerPickIds = playerPicks.stream()
                .map(DomainPick::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, List<DomainPickSubmission>> submissionsByPick = submissions.stream()
                .filter(submission -> playerPickIds.contains(submission.getPickId()))
                .collect(Collectors.groupingBy(DomainPickSubmission::getPickId));
        Map<Long, List<DomainGame>> gamesByWeek = games.stream()
                .collect(Collectors.groupingBy(game -> game.getWeek().getId()));

        int strikes = 0;
        for (DomainWeek week : weeks)
        {
            DomainPick pick = picksByWeek.get(week.getId());
            List<DomainGame> weekGames = gamesByWeek.getOrDefault(week.getId(), List.of());
            List<DomainPickSubmission> pickSubmissions = pick == null
                    ? List.of()
                    : submissionsByPick.getOrDefault(pick.getId(), List.of());
            strikes += calculateWeekStrike(rules, week, weekGames, pick, pickSubmissions, now);
        }
        return strikes;
    }

    /**
     * Calculates the strike, if any, attributable to one player's pick for a week.
     *
     * @param rules season rules governing deadlines and ties
     * @param week the week being evaluated
     * @param weekGames games scheduled in the week
     * @param pick the player's pick, or {@code null} if none exists
     * @param submissions submissions associated with the pick
     * @param now the instant at which to evaluate the week
     * @return one when the player missed the deadline or lost, otherwise zero
     */
    private int calculateWeekStrike(DomainRules rules, DomainWeek week, List<DomainGame> weekGames,
                                    DomainPick pick, List<DomainPickSubmission> submissions, Instant now)
    {
        if (isMissedPick(rules, week, weekGames, submissions, now))
        {
            return 1;
        }
        return isLosingPick(rules, pick, weekGames) ? 1 : 0;
    }

    /**
     * Determines whether the player's effective pick lost its completed game.
     *
     * @param rules season rules governing tie safety
     * @param pick the player's effective pick, or {@code null} if none exists
     * @param weekGames games scheduled in the pick's week
     * @return {@code true} when the selected team lost or an unsafe tie occurred
     */
    private boolean isLosingPick(DomainRules rules, DomainPick pick, List<DomainGame> weekGames)
    {
        if (pick == null || pick.getTeamSeasonId() == null)
        {
            return false;
        }
        DomainGame selectedGame = weekGames.stream()
                .filter(game -> pick.getTeamSeasonId().equals(game.getHomeTeamSeasonId())
                        || pick.getTeamSeasonId().equals(game.getAwayTeamSeasonId()))
                .findFirst()
                .orElse(null);
        if (selectedGame == null || selectedGame.getStatus() != GameStatus.FINAL)
        {
            return false;
        }
        GameOutcome outcome = selectedGame.getOutcome();
        boolean selectedTeamWon = (outcome == GameOutcome.HOME_WIN
                && pick.getTeamSeasonId().equals(selectedGame.getHomeTeamSeasonId()))
                || (outcome == GameOutcome.HOME_LOSS
                && pick.getTeamSeasonId().equals(selectedGame.getAwayTeamSeasonId()));
        return !selectedTeamWon && (outcome != GameOutcome.TIE || !Boolean.TRUE.equals(rules.getTiesAreSafe()));
    }

    /**
     * Determines whether the player missed a pick deadline for the week.
     *
     * @param rules season rules governing the pick deadline
     * @param week the week and its date range
     * @param weekGames games scheduled in the week
     * @param submissions submissions associated with the player's pick
     * @param now the instant at which to evaluate the deadline
     * @return {@code true} when no timely submission exists and the deadline has passed
     */
    private boolean isMissedPick(DomainRules rules, DomainWeek week,
                                 List<DomainGame> weekGames, List<DomainPickSubmission> submissions,
                                 Instant now)
    {
        if (hasSubmissionForWeek(rules, week, weekGames, submissions, now))
        {
            return false;
        }
        Instant deadline = Boolean.TRUE.equals(rules.getAllPicksBeforeWeek())
                ? allPicksDeadline(week, weekGames)
                : week.getEndDate().atStartOfDay(EASTERN).toInstant();
        return deadline != null && !now.isBefore(deadline);
    }

    /**
     * Checks whether a submission was made in the valid submission window for the week.
     *
     * @param rules season rules governing the submission window
     * @param week the week and its date range
     * @param weekGames games scheduled in the week
     * @param submissions submissions associated with the player's pick
     * @param now the latest instant to include
     * @return {@code true} if a submission exists in the valid window
     */
    private boolean hasSubmissionForWeek(DomainRules rules, DomainWeek week,
                                         List<DomainGame> weekGames,
                                         List<DomainPickSubmission> submissions, Instant now)
    {
        if (Boolean.TRUE.equals(rules.getAllPicksBeforeWeek()))
        {
            Instant deadline = allPicksDeadline(week, weekGames);
            if (deadline == null)
            {
                return false;
            }
            return submissions.stream().anyMatch(submission ->
                    submission.getSubmittedAt().isBefore(deadline)
                            && !submission.getSubmittedAt().isAfter(now));
        }
        Instant start = week.getStartDate().atStartOfDay(EASTERN).toInstant();
        Instant end = week.getEndDate().atStartOfDay(EASTERN).toInstant();
        return submissions.stream().anyMatch(submission ->
                !submission.getSubmittedAt().isBefore(start)
                        && submission.getSubmittedAt().isBefore(end)
                        && !submission.getSubmittedAt().isAfter(now));
    }

    /**
     * Finds the all-picks deadline from the first game start on the week start date or its fallback.
     *
     * @param week the week and its date range
     * @param weekGames games scheduled in the week
     * @return the deadline instant, or {@code null} while a scheduled start-day game has not started
     */
    private Instant allPicksDeadline(DomainWeek week, List<DomainGame> weekGames)
    {
        LocalDate weekStart = week.getStartDate();
        List<Instant> startsOnWeekStart = weekGames.stream()
                .filter(game -> game.getGameTime() != null
                        && game.getGameTime().atZone(EASTERN).toLocalDate().equals(weekStart))
                .filter(game -> game.getStatus() != GameStatus.POSTPONED
                        && game.getStatus() != GameStatus.CANCELLED)
                .map(DomainGame::getActualStartTime)
                .filter(Objects::nonNull)
                .sorted()
                .toList();
        if (!startsOnWeekStart.isEmpty())
        {
            return startsOnWeekStart.getFirst();
        }
        boolean hasActiveScheduledGame = weekGames.stream()
                .filter(game -> game.getGameTime() != null
                        && game.getGameTime().atZone(EASTERN).toLocalDate().equals(weekStart))
                .anyMatch(game -> game.getStatus() != GameStatus.POSTPONED
                        && game.getStatus() != GameStatus.CANCELLED);
        if (hasActiveScheduledGame)
        {
            return null;
        }
        return weekStart.atTime(LocalTime.MAX).atZone(EASTERN).toInstant();
    }

}
