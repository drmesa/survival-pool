package com.nothingtothetable.survivalpool.api.v1;

import com.nothingtothetable.survivalpool.types.DomainPick;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Player
{
    private Long id;
    private String name;
    private Integer strikes;
    private DomainPick currentPick;
    private Map<Long, List<DomainPick>> pastPicks;
}
