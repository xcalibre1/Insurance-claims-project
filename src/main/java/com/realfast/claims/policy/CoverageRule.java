package com.realfast.claims.policy;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

/**
 * Whether a procedure is covered under a plan and the maximum payable amount per line.
 */
@Getter
@Builder
public class CoverageRule {

    private final String procedureCode;
    private final boolean covered;
    private final BigDecimal maxApprovedAmount;

    public static CoverageRule covered(String procedureCode, BigDecimal maxApprovedAmount) {
        return CoverageRule.builder()
                .procedureCode(procedureCode)
                .covered(true)
                .maxApprovedAmount(maxApprovedAmount)
                .build();
    }

    public static CoverageRule notCovered(String procedureCode) {
        return CoverageRule.builder()
                .procedureCode(procedureCode)
                .covered(false)
                .maxApprovedAmount(BigDecimal.ZERO)
                .build();
    }
}
