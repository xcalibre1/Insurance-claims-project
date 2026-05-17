package com.realfast.claims.web.dto;

import com.realfast.claims.model.Claim;
import com.realfast.claims.model.ClaimLine;
import com.realfast.claims.model.LineAdjustment;

public final class ClaimMapper {

    private ClaimMapper() {}

    public static Claim toClaim(SubmitClaimRequest request) {
        return new Claim(
                request.memberId(),
                request.providerId(),
                request.planId(),
                request.serviceFrom(),
                request.serviceTo());
    }

    public static ClaimLine toLine(SubmitClaimLineRequest request) {
        return new ClaimLine(
                request.lineNumber(),
                request.procedureCode(),
                request.diagnosisCode(),
                request.serviceDate(),
                request.units(),
                request.billedAmount());
    }

    public static ClaimResponse toResponse(Claim claim) {
        return ClaimResponse.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .memberId(claim.getMemberId())
                .providerId(claim.getProviderId())
                .planId(claim.getPlanId())
                .serviceFrom(claim.getServiceFrom())
                .serviceTo(claim.getServiceTo())
                .status(claim.getStatus())
                .outcome(claim.getOutcome())
                .submittedAt(claim.getSubmittedAt())
                .adjudicatedAt(claim.getAdjudicatedAt())
                .totalBilled(claim.getTotalBilled())
                .totalApproved(claim.getTotalApproved())
                .lines(claim.getLines().stream().map(ClaimMapper::toLineResponse).toList())
                .build();
    }

    private static ClaimLineResponse toLineResponse(ClaimLine line) {
        return ClaimLineResponse.builder()
                .id(line.getId())
                .lineNumber(line.getLineNumber())
                .procedureCode(line.getProcedureCode())
                .diagnosisCode(line.getDiagnosisCode())
                .serviceDate(line.getServiceDate())
                .units(line.getUnits())
                .billedAmount(line.getBilledAmount())
                .approvedAmount(line.getApprovedAmount())
                .status(line.getStatus())
                .adjustments(line.getAdjustments().stream().map(ClaimMapper::toAdjustmentResponse).toList())
                .build();
    }

    private static LineAdjustmentResponse toAdjustmentResponse(LineAdjustment adjustment) {
        return LineAdjustmentResponse.builder()
                .code(adjustment.getCode())
                .type(adjustment.getType())
                .description(adjustment.getDescription())
                .amount(adjustment.getAmount())
                .build();
    }
}
