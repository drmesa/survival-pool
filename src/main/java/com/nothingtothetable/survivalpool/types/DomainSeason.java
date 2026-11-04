package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class DomainSeason
{
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfWeeks;
    private Long rulesId;
}
