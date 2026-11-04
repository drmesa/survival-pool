package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DomainPlayerPage
{
    private DomainPlayer player;
    private PlayerSeasonSummary currentSeason;
}
