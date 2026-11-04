package com.nothingtothetable.survivalpool.repository;

import com.nothingtothetable.survivalpool.types.DomainPickSubmission;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface PickSubmissionRepository
{
    @Insert("""
            INSERT INTO pick_submissions (
                pick_id, team_season_id, submitted_at, submitted_by_user_id,
                event_type, correction_reason
            ) VALUES (
                #{pickId}, #{teamSeasonId}, #{submittedAt}, #{submittedByUserId},
                #{eventType}, #{correctionReason}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DomainPickSubmission submission);
}
