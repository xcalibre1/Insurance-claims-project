package com.realfast.claims.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.realfast.claims.exception.ClaimNotFoundException;
import com.realfast.claims.model.ClaimStatus;
import com.realfast.claims.policy.PolicyCatalog;
import com.realfast.claims.web.dto.SubmitClaimLineRequest;
import com.realfast.claims.web.dto.SubmitClaimRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ClaimServiceTest {

    @Autowired
    private ClaimService claimService;

    @Test
    void submitAndAdjudicateFlow() {
        var response = claimService.submit(sampleSubmitRequest());
        assertThat(response.status()).isEqualTo(ClaimStatus.SUBMITTED);
        assertThat(response.totalBilled()).isEqualByComparingTo("80.00");

        var adjudicated = claimService.adjudicate(response.id());
        assertThat(adjudicated.status()).isEqualTo(ClaimStatus.ADJUDICATED);
        assertThat(adjudicated.totalApproved()).isEqualByComparingTo("80.00");
        assertThat(adjudicated.lines()).hasSize(1);
        assertThat(adjudicated.lines().getFirst().adjustments()).isEmpty();
    }

    @Test
    void getByIdNotFound() {
        assertThatThrownBy(() -> claimService.getById(UUID.randomUUID()))
                .isInstanceOf(ClaimNotFoundException.class);
    }

    @Test
    void getByStatus() {
        claimService.submit(sampleSubmitRequest());
        assertThat(claimService.getByStatus(ClaimStatus.SUBMITTED)).hasSize(1);
    }

    private static SubmitClaimRequest sampleSubmitRequest() {
        return new SubmitClaimRequest(
                "M-1",
                "P-1",
                "PLAN-BASIC",
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 1),
                List.of(new SubmitClaimLineRequest(
                        1, "99213", null, LocalDate.of(2026, 5, 1), 1, new BigDecimal("80.00"))));
    }
}
