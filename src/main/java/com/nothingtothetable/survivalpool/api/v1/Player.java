package com.nothingtothetable.survivalpool.api.v1;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Player
{
    private Long id;
    private String name;
}
