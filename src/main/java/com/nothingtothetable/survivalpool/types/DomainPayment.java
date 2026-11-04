package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Accessors(chain = true)
public class DomainPayment
{
    private Long id;
    private Long playerSeasonXrefId;
    private BigDecimal amount;
    private Instant paidAt;
}
