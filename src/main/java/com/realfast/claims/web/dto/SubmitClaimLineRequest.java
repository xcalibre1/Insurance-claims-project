package com.realfast.claims.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SubmitClaimLineRequest(
        @Min(1) int lineNumber,
        @NotBlank String procedureCode,
        String diagnosisCode,
        @NotNull LocalDate serviceDate,
        @Min(1) int units,
        @NotNull @DecimalMin("0.01") BigDecimal billedAmount) {}
