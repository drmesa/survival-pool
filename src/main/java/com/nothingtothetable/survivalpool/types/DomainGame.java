package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class DomainGame
{
    private Long id;
    private DomainWeek week;
    private Long homeTeamSeasonId;
    private Long awayTeamSeasonId;
    private Instant gameTime;
    private Instant actualStartTime;
    private GameStatus status;
    private Integer homeScore;
    private Integer awayScore;

    public GameOutcome getOutcome()
    {
        if (status != GameStatus.FINAL || homeScore == null || awayScore == null)
        {
            return null;
        }
        if (homeScore.equals(awayScore))
        {
            return GameOutcome.TIE;
        }
        return homeScore > awayScore ? GameOutcome.HOME_WIN : GameOutcome.HOME_LOSS;
    }
}
