package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PlayerSeasonSummary
{
    private Long id;
    private String name;
    private List<DomainPick> picks;
    private Integer strikes;
    private Boolean paidIn;
}
