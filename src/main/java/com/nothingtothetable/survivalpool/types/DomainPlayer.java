package com.nothingtothetable.survivalpool.types;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DomainPlayer
{
    private Long id;
    private DomainPick currentPick;
    private Map<Long, List<DomainPick>> pastPicks;
    private Integer strikes;
    private String name;
}
