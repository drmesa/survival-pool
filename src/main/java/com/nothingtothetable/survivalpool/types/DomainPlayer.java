package com.nothingtothetable.survivalpool.types;

import java.util.List;

public class DomainPlayer
{
    private Long id;
    private DomainPick currentPick;
    private List<DomainPick> pastPicks;
    private Integer strikes;
    private String name;

}
