package com.nothingtothetable.survivalpool.api.v1;

import com.nothingtothetable.survivalpool.types.PickOutcome;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.List;

@Data
@Accessors(chain = true)
public class PlayerPage
{
    private Player player;
    private CurrentSeason currentSeason;

    @Data
    @Accessors(chain = true)
    public static class CurrentSeason
    {
        private Long id;
        private String name;
        private List<Pick> picks;
        private Integer strikes;
        private Boolean paidIn;
    }

    @Data
    @Accessors(chain = true)
    public static class Pick
    {
        private Long id;
        private Integer weekNumber;
        private Long teamSeasonId;
        private String teamName;
        private String teamAbbreviation;
        private PickOutcome outcome;
        private Instant submittedAt;
    }
}
