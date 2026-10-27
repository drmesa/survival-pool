package com.nothingtothetable.survivalpool.types;

import lombok.Data;

import java.util.List;

@Data
public class DomainPlayer
{
    private Long id;
    private DomainPick currentPick;
    private List<DomainPick> pastPicks;
    private Integer strikes;
    private String name;
}
