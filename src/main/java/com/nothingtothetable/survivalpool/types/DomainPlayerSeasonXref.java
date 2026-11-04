package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DomainPlayerSeasonXref
{
    private Long id;
    private Long playerId;
    private Long seasonId;
}
