package com.realfast.claims.web.dto;

import com.realfast.claims.model.LineStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ClaimLineResponse(
        UUID id,
        int lineNumber,
        String procedureCode,
        String diagnosisCode,
        LocalDate serviceDate,
        int units,
        BigDecimal billedAmount,
        BigDecimal approvedAmount,
        LineStatus status,
        List<LineAdjustmentResponse> adjustments) {}
