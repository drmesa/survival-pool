package com.nothingtothetable.survivalpool.api.v1;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TeamSeason
{
    private Long id;
    private Long franchiseId;
    private Long seasonId;
    private String name;
    private String abbreviation;
}
