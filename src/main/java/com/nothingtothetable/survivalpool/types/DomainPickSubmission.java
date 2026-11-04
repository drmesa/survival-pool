package com.nothingtothetable.survivalpool.types;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class DomainPickSubmission
{
    private Long id;
    private Long pickId;
    private Long teamSeasonId;
    private Instant submittedAt;
    private Long submittedByUserId;
    private PickSubmissionType eventType;
    private String correctionReason;
}
