package com.realfast.claims.web.dto;

import com.realfast.claims.model.AdjustmentType;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record LineAdjustmentResponse(
        String code, AdjustmentType type, String description, BigDecimal amount) {}
