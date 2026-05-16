package com.realfast.claims.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "claims")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String claimNumber;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String planId;

    @Column(nullable = false)
    private LocalDate serviceFrom;

    @Column(nullable = false)
    private LocalDate serviceTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ClaimStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private ClaimOutcome outcome;

    @Column(nullable = false)
    private Instant submittedAt;

    private Instant adjudicatedAt;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalBilled;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalApproved;

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lineNumber ASC")
    private List<ClaimLine> lines = new ArrayList<>();

    public Claim(
            String memberId,
            String providerId,
            String planId,
            LocalDate serviceFrom,
            LocalDate serviceTo) {
        this.claimNumber = UUID.randomUUID().toString();
        this.memberId = memberId;
        this.providerId = providerId;
        this.planId = planId;
        this.serviceFrom = serviceFrom;
        this.serviceTo = serviceTo;
        this.status = ClaimStatus.SUBMITTED;
        this.submittedAt = Instant.now();
        this.totalBilled = BigDecimal.ZERO;
        this.totalApproved = BigDecimal.ZERO;
    }

    public List<ClaimLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public void addLine(ClaimLine line) {
        lines.add(line);
        line.setClaim(this);
    }

    public void recalculateTotals() {
        totalBilled = lines.stream()
                .map(ClaimLine::getBilledAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalApproved = lines.stream()
                .map(ClaimLine::approvedAmountOrZero)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * APPROVED — every line fully approved; DENIED — every line denied; otherwise PARTIAL.
     */
    public ClaimOutcome deriveOutcome() {
        if (lines.isEmpty()) {
            return null;
        }
        boolean allApproved = lines.stream().allMatch(line -> line.getStatus() == LineStatus.APPROVED);
        if (allApproved) {
            return ClaimOutcome.APPROVED;
        }
        boolean allDenied = lines.stream().allMatch(line -> line.getStatus() == LineStatus.DENIED);
        if (allDenied) {
            return ClaimOutcome.DENIED;
        }
        return ClaimOutcome.PARTIAL;
    }

    public void finalizeAdjudication() {
        recalculateTotals();
        outcome = deriveOutcome();
        status = ClaimStatus.ADJUDICATED;
        adjudicatedAt = Instant.now();
    }

    public void assertSubmitted() {
        if (status != ClaimStatus.SUBMITTED) {
            throw new IllegalStateException("Claim must be SUBMITTED, was " + status);
        }
    }
}
