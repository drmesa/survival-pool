package com.nothingtothetable.survivalpool.service;

import com.nothingtothetable.survivalpool.repository.GameRepository;
import com.nothingtothetable.survivalpool.repository.PaymentRepository;
import com.nothingtothetable.survivalpool.repository.PickRepository;
import com.nothingtothetable.survivalpool.repository.PlayerRepository;
import com.nothingtothetable.survivalpool.repository.PlayerSeasonRepository;
import com.nothingtothetable.survivalpool.repository.RulesRepository;
import com.nothingtothetable.survivalpool.repository.SeasonRepository;
import com.nothingtothetable.survivalpool.repository.WeekRepository;
import com.nothingtothetable.survivalpool.types.DomainGame;
import com.nothingtothetable.survivalpool.types.DomainPick;
import com.nothingtothetable.survivalpool.types.DomainPlayer;
import com.nothingtothetable.survivalpool.types.DomainPlayerPage;
import com.nothingtothetable.survivalpool.types.DomainPlayerSeasonXref;
import com.nothingtothetable.survivalpool.types.PlayerSeasonSummary;
import com.nothingtothetable.survivalpool.types.DomainRules;
import com.nothingtothetable.survivalpool.types.DomainSeason;
import com.nothingtothetable.survivalpool.types.DomainWeek;
import com.nothingtothetable.survivalpool.types.GameOutcome;
import com.nothingtothetable.survivalpool.types.GameStatus;
import com.nothingtothetable.survivalpool.types.PickOutcome;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerPageService
{
    private static final ZoneId EASTERN = ZoneId.of("America/New_York");

    private final PlayerRepository playerRepository;
    private final SeasonRepository seasonRepository;
    private final PlayerSeasonRepository playerSeasonRepository;
    private final RulesRepository rulesRepository;
    private final PaymentRepository paymentRepository;
    private final WeekRepository weekRepository;
    private final GameRepository gameRepository;
    private final PickRepository pickRepository;
    private final StrikeCalculator strikeCalculator;

    public PlayerPageService(PlayerRepository playerRepository, SeasonRepository seasonRepository,
                             PlayerSeasonRepository playerSeasonRepository, RulesRepository rulesRepository,
                             PaymentRepository paymentRepository, WeekRepository weekRepository,
                             GameRepository gameRepository, PickRepository pickRepository,
                             StrikeCalculator strikeCalculator)
    {
        this.playerRepository = playerRepository;
        this.seasonRepository = seasonRepository;
        this.playerSeasonRepository = playerSeasonRepository;
        this.rulesRepository = rulesRepository;
        this.paymentRepository = paymentRepository;
        this.weekRepository = weekRepository;
        this.gameRepository = gameRepository;
        this.pickRepository = pickRepository;
        this.strikeCalculator = strikeCalculator;
    }

    public Optional<DomainPlayerPage> findById(Long playerId)
    {
        DomainPlayer player = playerRepository.findById(playerId);
        if (player == null)
        {
            return Optional.empty();
        }

        DomainPlayerPage page = new DomainPlayerPage().setPlayer(player);
        DomainSeason season = seasonRepository.findCurrent(LocalDate.now(EASTERN));
        if (season == null)
        {
            return Optional.of(page);
        }

        DomainPlayerSeasonXref participation =
                playerSeasonRepository.findByPlayerAndSeason(playerId, season.getId());
        if (participation == null)
        {
            return Optional.of(page);
        }

        DomainRules rules = rulesRepository.findById(season.getRulesId());
        if (rules == null)
        {
            throw new IllegalStateException("Season " + season.getId() + " references missing rules "
                    + season.getRulesId());
        }

        List<DomainWeek> seasonWeeks = weekRepository.findBySeasonId(season.getId());
        List<DomainPick> picks = pickRepository.findByPlayerSeason(participation.getId());
        List<DomainGame> games = gameRepository.findBySeasonId(season.getId());
        populateOutcomes(picks, games);

        BigDecimal paidAmount = paymentRepository.totalForPlayerSeason(participation.getId());
        PlayerSeasonSummary currentSeason = new PlayerSeasonSummary()
                .setId(season.getId())
                .setName(season.getName())
                .setPicks(picks)
                .setStrikes(strikeCalculator.calculate(
                        participation.getId(),
                        rules,
                        seasonWeeks,
                        games,
                        picks,
                        pickRepository.findSubmissionsByPlayerSeason(participation.getId()),
                        Instant.now()))
                .setPaidIn(paidAmount.compareTo(rules.getEntryFee()) >= 0);
        page.setCurrentSeason(currentSeason);
        return Optional.of(page);
    }

    /**
     * Sets outcomes for picks whose selected teams have completed games, leaving unresolved picks unchanged.
     *
     * @param picks the picks whose outcomes should be populated
     * @param games the games used to resolve each selected team's result
     */
    private void populateOutcomes(List<DomainPick> picks, List<DomainGame> games)
    {
        Map<Long, List<DomainGame>> gamesByWeek = games.stream()
                .collect(Collectors.groupingBy(game -> game.getWeek().getId()));
        for (DomainPick pick : picks)
        {
            if (pick.getTeamSeasonId() == null)
            {
                continue;
            }
            DomainGame game = gamesByWeek.getOrDefault(pick.getWeekId(), List.of()).stream()
                    .filter(candidate -> pick.getTeamSeasonId().equals(candidate.getHomeTeamSeasonId())
                            || pick.getTeamSeasonId().equals(candidate.getAwayTeamSeasonId()))
                    .findFirst()
                    .orElse(null);
            if (game == null || game.getStatus() != GameStatus.FINAL)
            {
                continue;
            }
            GameOutcome outcome = game.getOutcome();
            if (outcome == GameOutcome.TIE)
            {
                pick.setOutcome(PickOutcome.TIE);
            }
            else if ((outcome == GameOutcome.HOME_WIN
                    && pick.getTeamSeasonId().equals(game.getHomeTeamSeasonId()))
                    || (outcome == GameOutcome.HOME_LOSS
                    && pick.getTeamSeasonId().equals(game.getAwayTeamSeasonId())))
            {
                pick.setOutcome(PickOutcome.HIT);
            }
            else
            {
                pick.setOutcome(PickOutcome.MISS);
            }
        }
    }
}
