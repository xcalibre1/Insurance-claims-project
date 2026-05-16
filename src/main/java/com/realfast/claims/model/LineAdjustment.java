package com.realfast.claims.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Explains why a line was denied or paid less than billed.
 * Owned by {@link ClaimLine} (no separate identity).
 */
@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LineAdjustment {

    @Column(nullable = false, length = 32)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AdjustmentType type;

    @Column(nullable = false, length = 512)
    private String description;

    /** Optional dollar impact (e.g. billed minus approved). */
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    public static LineAdjustment denial(String code, String description) {
        return LineAdjustment.builder()
                .code(code)
                .type(AdjustmentType.DENIAL)
                .description(description)
                .amount(BigDecimal.ZERO)
                .build();
    }

    public static LineAdjustment reduction(String code, String description, BigDecimal amount) {
        return LineAdjustment.builder()
                .code(code)
                .type(AdjustmentType.REDUCTION)
                .description(description)
                .amount(amount)
                .build();
    }
}
