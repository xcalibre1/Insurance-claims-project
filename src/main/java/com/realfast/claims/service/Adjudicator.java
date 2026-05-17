package com.realfast.claims.service;

import com.realfast.claims.model.Claim;
import com.realfast.claims.model.ClaimLine;
import com.realfast.claims.model.LineAdjustment;
import com.realfast.claims.model.LineStatus;
import com.realfast.claims.policy.CoverageRule;
import com.realfast.claims.policy.Policy;
import com.realfast.claims.policy.PolicyCatalog;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Runs coverage rules on each line, then rolls up claim totals and outcome.
 */
@Service
public class Adjudicator {

    private final PolicyCatalog policyCatalog;

    public Adjudicator(PolicyCatalog policyCatalog) {
        this.policyCatalog = policyCatalog;
    }

    public void adjudicate(Claim claim) {
        claim.assertSubmitted();
        Policy policy = policyCatalog.requirePolicy(claim.getPlanId());

        for (ClaimLine line : claim.getLines()) {
            adjudicateLine(line, policy);
        }

        claim.finalizeAdjudication();
    }

    private void adjudicateLine(ClaimLine line, Policy policy) {
        line.clearAdjudication();

        BigDecimal billed = line.getBilledAmount();
        var ruleOptional = policy.ruleFor(line.getProcedureCode());

        if (ruleOptional.isEmpty()) {
            deny(line, "NOT_ON_PLAN", "Procedure is not listed on this plan");
            return;
        }

        CoverageRule rule = ruleOptional.get();
        if (!rule.isCovered()) {
            deny(line, "NOT_COVERED", "Procedure is excluded from coverage");
            return;
        }

        BigDecimal cap = rule.getMaxApprovedAmount();
        BigDecimal approved = billed.min(cap);

        List<LineAdjustment> adjustments = new ArrayList<>();
        if (approved.compareTo(billed) < 0) {
            adjustments.add(LineAdjustment.reduction(
                    "CONTRACTUAL",
                    "Approved amount is less than the billed amount",
                    billed.subtract(approved)));
        }

        LineStatus status = ClaimLine.statusFromAmounts(billed, approved);
        line.applyAdjudication(approved, status, adjustments);
    }

    private void deny(ClaimLine line, String code, String description) {
        line.applyAdjudication(
                BigDecimal.ZERO,
                LineStatus.DENIED,
                List.of(LineAdjustment.denial(code, description)));
    }
}
