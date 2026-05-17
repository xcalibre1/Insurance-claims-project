package com.realfast.claims.web.dto;

import com.realfast.claims.model.ClaimOutcome;
import com.realfast.claims.model.ClaimStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ClaimResponse(
        UUID id,
        String claimNumber,
        String memberId,
        String providerId,
        String planId,
        LocalDate serviceFrom,
        LocalDate serviceTo,
        ClaimStatus status,
        ClaimOutcome outcome,
        Instant submittedAt,
        Instant adjudicatedAt,
        BigDecimal totalBilled,
        BigDecimal totalApproved,
        List<ClaimLineResponse> lines) {}
