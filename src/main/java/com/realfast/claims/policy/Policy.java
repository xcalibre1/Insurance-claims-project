package com.realfast.claims.policy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * Plan-level coverage: which procedure codes are covered and their payment caps.
 */
@Getter
public class Policy {

    private final String planId;
    private final Map<String, CoverageRule> rulesByProcedure;

    @Builder
    private Policy(String planId, @Singular List<CoverageRule> rules) {
        this.planId = planId;
        this.rulesByProcedure = rules.stream()
                .collect(Collectors.toUnmodifiableMap(rule -> normalize(rule.getProcedureCode()), rule -> rule));
    }

    public Optional<CoverageRule> ruleFor(String procedureCode) {
        return Optional.ofNullable(rulesByProcedure.get(normalize(procedureCode)));
    }

    static String normalize(String procedureCode) {
        return procedureCode == null ? "" : procedureCode.trim().toLowerCase();
    }
}
