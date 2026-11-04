package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DomainPlayer
{
    private Long id;
    private Long userId;
    private String name;
}
