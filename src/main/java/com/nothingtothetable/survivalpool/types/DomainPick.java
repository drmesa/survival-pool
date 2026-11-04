package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class DomainPick
{
    private Long id;
    private Long playerSeasonXrefId;
    private Long weekId;
    private Integer weekNumber;
    private Long teamSeasonId;
    private String teamName;
    private String teamAbbreviation;
    private Instant submittedAt;
    private PickOutcome outcome;
}
