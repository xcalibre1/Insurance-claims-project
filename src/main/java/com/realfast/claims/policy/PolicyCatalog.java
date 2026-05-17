package com.realfast.claims.policy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * In-memory plan policies for the MVP (replace with DB/config later).
 */
@Component
public class PolicyCatalog {

    private final Map<String, Policy> policies = new ConcurrentHashMap<>();

    public PolicyCatalog() {
        registerDefaultPolicies();
    }

    public Policy requirePolicy(String planId) {
        Policy policy = policies.get(planId);
        if (policy == null) {
            throw new IllegalArgumentException("Unknown plan: " + planId);
        }
        return policy;
    }

    private void registerDefaultPolicies() {
        Policy basic = Policy.builder()
                .planId("PLAN-BASIC")
                .rule(CoverageRule.covered("99213", new BigDecimal("120.00")))
                .rule(CoverageRule.covered("99214", new BigDecimal("180.00")))
                .rule(CoverageRule.covered("office-visit", new BigDecimal("100.00")))
                .rule(CoverageRule.notCovered("experimental"))
                .build();
        policies.put(basic.getPlanId(), basic);
    }
}
