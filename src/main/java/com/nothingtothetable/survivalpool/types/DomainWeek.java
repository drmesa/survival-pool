package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class DomainWeek
{
    private Long id;
    private Long seasonId;
    private Integer weekNumber;
    private LocalDate startDate;
    private LocalDate endDate;
}
