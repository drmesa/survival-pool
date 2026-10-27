package com.nothingtothetable.survivalpool.api.v1;

import com.nothingtothetable.survivalpool.types.DomainPick;
import lombok.Data;

import java.util.List;

@Data
public class ApiPlayer
{
    private Long id;
    private String name;
    private Integer strikes;
    private DomainPick currentPick;
    private List<DomainPick> pastPicks;
}
