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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class PickSubmissionService
{
    private static final ZoneId EASTERN = ZoneId.of("America/New_York");

    private final PlayerRepository playerRepository;
    private final PlayerSeasonRepository playerSeasonRepository;
    private final SeasonRepository seasonRepository;
    private final RulesRepository rulesRepository;
    private final WeekRepository weekRepository;
    private final TeamSeasonRepository teamSeasonRepository;
    private final GameRepository gameRepository;
    private final PickRepository pickRepository;
    private final PickSubmissionRepository submissionRepository;

    public PickSubmissionService(PlayerRepository playerRepository,
                                 PlayerSeasonRepository playerSeasonRepository,
                                 SeasonRepository seasonRepository,
                                 RulesRepository rulesRepository,
                                 WeekRepository weekRepository,
                                 TeamSeasonRepository teamSeasonRepository,
                                 GameRepository gameRepository,
                                 PickRepository pickRepository,
                                 PickSubmissionRepository submissionRepository)
    {
        this.playerRepository = playerRepository;
        this.playerSeasonRepository = playerSeasonRepository;
        this.seasonRepository = seasonRepository;
        this.rulesRepository = rulesRepository;
        this.weekRepository = weekRepository;
        this.teamSeasonRepository = teamSeasonRepository;
        this.gameRepository = gameRepository;
        this.pickRepository = pickRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional
    public DomainPickSubmission submit(Long playerSeasonXrefId, Long weekId, Long teamSeasonId, Long userId)
    {
        Instant now = Instant.now();
        DomainPick pick = validatePick(playerSeasonXrefId, weekId, teamSeasonId, userId, now);
        if (pick == null)
        {
            pick = new DomainPick()
                    .setPlayerSeasonXrefId(playerSeasonXrefId)
                    .setWeekId(weekId);
            if (pickRepository.insert(pick) != 1)
            {
                throw new IllegalStateException("Could not create the pick slot");
            }
        }
        return saveSubmission(pick.getId(), teamSeasonId, userId, now,
                PickSubmissionType.SUBMISSION, null);
    }

    /**
     * Loads the pick context and validates that a submission satisfies ownership, season, lock, and reuse rules.
     *
     * @param playerSeasonXrefId the player's season participation ID
     * @param weekId the target week ID
     * @param teamSeasonId the selected season team ID
     * @param userId the ID of the user submitting the pick
     * @param now the submission timestamp used for lock and reuse checks
     * @return the existing pick slot, or {@code null} if none exists yet
     * @throws NoSuchElementException if the participation, week, season, rules, or team does not exist
     * @throws IllegalArgumentException if the participation or team does not belong to the target season,
     *                                  or the team has no game in the week
     * @throws SecurityException if the user does not own the player entry
     * @throws IllegalStateException if the pick is locked, the selected team's game has started,
     *                                or the team has already been used
     */
    private DomainPick validatePick(Long playerSeasonXrefId, Long weekId, Long teamSeasonId,
                                    Long userId, Instant now)
    {
        DomainPlayerSeasonXref participation = requiredParameterCheck(
                playerSeasonRepository.findById(playerSeasonXrefId), "Player-season entry", playerSeasonXrefId);
        DomainWeek week = requiredParameterCheck(weekRepository.findById(weekId), "Week", weekId);
        DomainSeason season = requiredParameterCheck(seasonRepository.findById(week.getSeasonId()),
                "Season", week.getSeasonId());
        requiredParticipationSeasonCheck(participation, season);
        DomainRules rules = requiredParameterCheck(rulesRepository.findById(season.getRulesId()),
                "Season rules", season.getRulesId());
        DomainTeamSeason team = requiredParameterCheck(teamSeasonRepository.findById(teamSeasonId),
                "Season team", teamSeasonId);
        requiredTeamSeasonCheck(team, season);
        List<DomainGame> games = gameRepository.findByWeekId(weekId);
        DomainGame selectedGame = gameForTeam(games, teamSeasonId);
        requiredPlayerUserCheck(participation, userId);

        DomainPick pick = pickRepository.findByPlayerSeasonAndWeek(playerSeasonXrefId, weekId);
        Long existingPickId = pick == null ? null : pick.getId();
        DomainPickSubmission current = existingPickId == null ? null
                : pickRepository.findSubmissionsByPlayerSeason(playerSeasonXrefId).stream()
                .filter(submission -> submission.getPickId().equals(existingPickId))
                .max(java.util.Comparator.comparing(DomainPickSubmission::getSubmittedAt)
                        .thenComparing(DomainPickSubmission::getId))
                .orElse(null);
        Long lockedTeam = current == null ? teamSeasonId : current.getTeamSeasonId();
        if (isLocked(rules, week, games, lockedTeam, now))
        {
            throw new IllegalStateException("The pick is locked");
        }
        if (selectedGame.getActualStartTime() != null
                && !now.isBefore(selectedGame.getActualStartTime()))
        {
            throw new IllegalStateException("A team cannot be picked after its game starts");
        }
        if (!Boolean.TRUE.equals(rules.getReuseTeams())
                && gameRepository.wasTeamUsed(playerSeasonXrefId, teamSeasonId, now))
        {
            throw new IllegalStateException("The team has already been used this season");
        }

        return pick;
    }

    @Transactional
    public DomainPickSubmission correct(Long playerSeasonXrefId, Long weekId, Long teamSeasonId,
                                        Long adminUserId, String reason)
    {
        if (reason == null || reason.isBlank())
        {
            throw new IllegalArgumentException("An administrative correction requires a reason");
        }
        DomainPlayerSeasonXref participation = requiredParameterCheck(
                playerSeasonRepository.findById(playerSeasonXrefId), "Player-season entry", playerSeasonXrefId);
        DomainWeek week = requiredParameterCheck(weekRepository.findById(weekId), "Week", weekId);
        DomainSeason season = requiredParameterCheck(seasonRepository.findById(week.getSeasonId()),
                "Season", week.getSeasonId());
        requiredParticipationSeasonCheck(participation, season);
        DomainTeamSeason team = requiredParameterCheck(teamSeasonRepository.findById(teamSeasonId),
                "Season team", teamSeasonId);
        requiredTeamSeasonCheck(team, season);
        gameForTeam(gameRepository.findByWeekId(weekId), teamSeasonId);

        DomainPick pick = pickRepository.findByPlayerSeasonAndWeek(playerSeasonXrefId, weekId);
        if (pick == null)
        {
            throw new NoSuchElementException("There is no pick to correct for this week");
        }
        boolean hasSubmission = pickRepository.findSubmissionsByPlayerSeason(playerSeasonXrefId).stream()
                .anyMatch(submission -> submission.getPickId().equals(pick.getId()));
        if (!hasSubmission)
        {
            throw new NoSuchElementException("There is no pick submission to correct");
        }
        return saveSubmission(pick.getId(), teamSeasonId, adminUserId, Instant.now(),
                PickSubmissionType.ADMIN_CORRECTION, reason.trim());
    }

    private DomainPickSubmission saveSubmission(Long pickId, Long teamSeasonId, Long userId,
                                                Instant submittedAt, PickSubmissionType eventType,
                                                String correctionReason)
    {
        DomainPickSubmission submission = new DomainPickSubmission()
                .setPickId(pickId)
                .setTeamSeasonId(teamSeasonId)
                .setSubmittedAt(submittedAt)
                .setSubmittedByUserId(userId)
                .setEventType(eventType)
                .setCorrectionReason(correctionReason);
        if (submissionRepository.insert(submission) != 1)
        {
            throw new IllegalStateException("Could not save the pick submission");
        }
        return submission;
    }

    /**
     * Determines whether a pick is locked under the season's deadline rule.
     *
     * @param rules rules governing whether all picks lock together
     * @param week the week and its date range
     * @param games games scheduled in the week
     * @param selectedTeamSeasonId the selected team when locks are game-specific
     * @param now the instant at which to evaluate the lock
     * @return {@code true} when the applicable lock time has passed
     */
    private boolean isLocked(DomainRules rules, DomainWeek week, List<DomainGame> games,
                             Long selectedTeamSeasonId, Instant now)
    {
        if (Boolean.TRUE.equals(rules.getAllPicksBeforeWeek()))
        {
            LocalDate weekStart = week.getStartDate();
            return games.stream()
                    .filter(game -> game.getGameTime() != null
                            && game.getGameTime().atZone(EASTERN).toLocalDate().equals(weekStart))
                    .filter(game -> game.getStatus() != GameStatus.POSTPONED
                            && game.getStatus() != GameStatus.CANCELLED)
                    .map(DomainGame::getActualStartTime)
                    .filter(start -> start != null)
                    .min(Instant::compareTo)
                    .map(start -> !now.isBefore(start))
                    .orElseGet(() -> noStartDayGameDeadlinePassed(week, games, now));
        }
        DomainGame selectedGame = gameForTeam(games, selectedTeamSeasonId);
        return selectedGame.getActualStartTime() != null
                && !now.isBefore(selectedGame.getActualStartTime());
    }

    /**
     * Checks whether the fallback deadline has passed when no active game is scheduled on week start.
     *
     * @param week the week and its date range
     * @param games games scheduled in the week
     * @param now the instant at which to evaluate the deadline
     * @return {@code true} when the start date's 11:59 p.m. Eastern deadline has passed
     */
    private boolean noStartDayGameDeadlinePassed(DomainWeek week, List<DomainGame> games, Instant now)
    {
        LocalDate weekStart = week.getStartDate();
        boolean hasActiveScheduledGame = games.stream()
                .filter(game -> game.getGameTime() != null
                        && game.getGameTime().atZone(EASTERN).toLocalDate().equals(weekStart))
                .anyMatch(game -> game.getStatus() != GameStatus.POSTPONED
                        && game.getStatus() != GameStatus.CANCELLED);
        if (hasActiveScheduledGame)
        {
            return false;
        }
        return !now.isBefore(weekStart.atTime(LocalTime.MAX).atZone(EASTERN).toInstant());
    }

    private DomainGame gameForTeam(List<DomainGame> games, Long teamSeasonId)
    {
        return games.stream()
                .filter(game -> teamSeasonId.equals(game.getHomeTeamSeasonId())
                        || teamSeasonId.equals(game.getAwayTeamSeasonId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The selected team has no game this week"));
    }

    private void requiredTeamSeasonCheck(DomainTeamSeason team, DomainSeason season)
    {
        if (!Objects.equals(team.getSeasonId(), season.getId()))
        {
            throw new IllegalArgumentException("The selected team is not part of this season");
        }
    }

    private void requiredParticipationSeasonCheck(DomainPlayerSeasonXref participation, DomainSeason season)
    {
        if (!Objects.equals(participation.getSeasonId(), season.getId()))
        {
            throw new IllegalArgumentException("The player is not participating in this season");
        }
    }

    private void requiredPlayerUserCheck(DomainPlayerSeasonXref participation, Long userId)
    {
        DomainPlayer player = playerRepository.findById(participation.getPlayerId());
        if (player == null || !Objects.equals(player.getUserId(), userId))
        {
            throw new SecurityException("The user does not own this player entry");
        }
    }

    /**
     * Returns a required repository result or reports the missing entity and its identifier.
     *
     * @param value the repository result to validate
     * @param entityName the descriptive name of the required entity
     * @param id the identifier used to look up the entity
     * @param <T> the entity type
     * @return the non-null repository result
     * @throws NoSuchElementException if {@code value} is {@code null}
     */
    private <T> T requiredParameterCheck(T value, String entityName, Long id)
    {
        if (value == null)
        {
            throw new NoSuchElementException(entityName + " " + id + " not found");
        }
        return value;
    }

}
