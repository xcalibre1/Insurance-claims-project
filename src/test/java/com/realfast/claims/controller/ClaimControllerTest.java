package com.realfast.claims.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.realfast.claims.model.ClaimOutcome;
import com.realfast.claims.model.ClaimStatus;
import com.realfast.claims.service.ClaimService;
import com.realfast.claims.web.dto.ClaimResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ClaimController.class)
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClaimService claimService;

    @Test
    void submitReturns201() throws Exception {
        UUID id = UUID.randomUUID();
        when(claimService.submit(any())).thenReturn(sampleResponse(id, ClaimStatus.SUBMITTED, null));

        mockMvc.perform(post("/api/v1/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "memberId": "M-1",
                                  "providerId": "P-1",
                                  "planId": "PLAN-BASIC",
                                  "serviceFrom": "2026-05-01",
                                  "serviceTo": "2026-05-01",
                                  "lines": [{
                                    "lineNumber": 1,
                                    "procedureCode": "99213",
                                    "serviceDate": "2026-05-01",
                                    "units": 1,
                                    "billedAmount": 80.00
                                  }]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));

        verify(claimService).submit(any());
    }

    @Test
    void getByIdReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(claimService.getById(id)).thenReturn(sampleResponse(id, ClaimStatus.ADJUDICATED, ClaimOutcome.APPROVED));

        mockMvc.perform(get("/api/v1/claims/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outcome").value("APPROVED"));
    }

    @Test
    void getByStatusReturns200() throws Exception {
        when(claimService.getByStatus(ClaimStatus.SUBMITTED)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/claims").param("status", "SUBMITTED"))
                .andExpect(status().isOk());

        verify(claimService).getByStatus(ClaimStatus.SUBMITTED);
    }

    @Test
    void adjudicateReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(claimService.adjudicate(id)).thenReturn(sampleResponse(id, ClaimStatus.ADJUDICATED, ClaimOutcome.APPROVED));

        mockMvc.perform(post("/api/v1/claims/{id}/adjudicate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ADJUDICATED"));
    }

    private static ClaimResponse sampleResponse(UUID id, ClaimStatus status, ClaimOutcome outcome) {
        return ClaimResponse.builder()
                .id(id)
                .claimNumber("CLM-1")
                .memberId("M-1")
                .providerId("P-1")
                .planId("PLAN-BASIC")
                .serviceFrom(LocalDate.of(2026, 5, 1))
                .serviceTo(LocalDate.of(2026, 5, 1))
                .status(status)
                .outcome(outcome)
                .submittedAt(Instant.parse("2026-05-01T10:00:00Z"))
                .adjudicatedAt(outcome != null ? Instant.parse("2026-05-01T11:00:00Z") : null)
                .totalBilled(new BigDecimal("80.00"))
                .totalApproved(outcome != null ? new BigDecimal("80.00") : BigDecimal.ZERO)
                .lines(List.of())
                .build();
    }
}
