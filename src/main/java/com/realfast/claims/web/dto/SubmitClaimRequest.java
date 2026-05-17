package com.realfast.claims.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record SubmitClaimRequest(
        @NotBlank String memberId,
        @NotBlank String providerId,
        @NotBlank String planId,
        @NotNull LocalDate serviceFrom,
        @NotNull LocalDate serviceTo,
        @NotEmpty @Valid List<SubmitClaimLineRequest> lines) {}
