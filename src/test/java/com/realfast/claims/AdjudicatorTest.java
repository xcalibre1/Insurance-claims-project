package com.realfast.claims;

import static org.assertj.core.api.Assertions.assertThat;

import com.realfast.claims.model.Claim;
import com.realfast.claims.model.ClaimLine;
import com.realfast.claims.model.ClaimOutcome;
import com.realfast.claims.model.ClaimStatus;
import com.realfast.claims.model.LineStatus;
import com.realfast.claims.policy.PolicyCatalog;
import com.realfast.claims.service.Adjudicator;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdjudicatorTest {

    private Adjudicator adjudicator;

    @BeforeEach
    void setUp() {
        adjudicator = new Adjudicator(new PolicyCatalog());
    }

    @Test
    void fullyApprovesWhenBilledAtOrBelowCap() {
        Claim claim = claimWithLine("99213", "80.00");

        adjudicator.adjudicate(claim);

        var line = claim.getLines().getFirst();
        assertThat(line.getStatus()).isEqualTo(LineStatus.APPROVED);
        assertThat(line.getApprovedAmount()).isEqualByComparingTo("80.00");
        assertThat(line.getAdjustments()).isEmpty();
        assertThat(claim.getOutcome()).isEqualTo(ClaimOutcome.APPROVED);
    }

    @Test
    void reducesWhenBilledExceedsCap() {
        Claim claim = claimWithLine("office-visit", "150.00");

        adjudicator.adjudicate(claim);

        var line = claim.getLines().getFirst();
        assertThat(line.getStatus()).isEqualTo(LineStatus.REDUCED);
        assertThat(line.getApprovedAmount()).isEqualByComparingTo("100.00");
        assertThat(line.getAdjustments()).hasSize(1);
        assertThat(line.getAdjustments().getFirst().getCode()).isEqualTo("CONTRACTUAL");
    }

    @Test
    void deniesExcludedProcedure() {
        Claim claim = claimWithLine("experimental", "500.00");

        adjudicator.adjudicate(claim);

        var line = claim.getLines().getFirst();
        assertThat(line.getStatus()).isEqualTo(LineStatus.DENIED);
        assertThat(line.getApprovedAmount()).isEqualByComparingTo("0");
        assertThat(claim.getOutcome()).isEqualTo(ClaimOutcome.DENIED);
    }

    @Test
    void partialClaimWhenLinesDiffer() {
        Claim claim = newClaim();
        claim.addLine(line(1, "99213", "80.00"));
        claim.addLine(line(2, "experimental", "200.00"));

        adjudicator.adjudicate(claim);

        assertThat(claim.getStatus()).isEqualTo(ClaimStatus.ADJUDICATED);
        assertThat(claim.getOutcome()).isEqualTo(ClaimOutcome.PARTIAL);
        assertThat(claim.getTotalBilled()).isEqualByComparingTo("280.00");
        assertThat(claim.getTotalApproved()).isEqualByComparingTo("80.00");
    }

    private static Claim claimWithLine(String procedureCode, String billed) {
        Claim claim = newClaim();
        claim.addLine(line(1, procedureCode, billed));
        return claim;
    }

    private static Claim newClaim() {
        return new Claim("M-1", "P-1", "PLAN-BASIC", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1));
    }

    private static ClaimLine line(int lineNumber, String procedureCode, String billed) {
        return new ClaimLine(
                lineNumber,
                procedureCode,
                "dx",
                LocalDate.of(2026, 5, 1),
                1,
                new BigDecimal(billed));
    }
}
