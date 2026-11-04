package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DomainTeamSeason
{
    private Long id;
    private Long franchiseId;
    private Long seasonId;
    private String name;
    private String abbreviation;
}
