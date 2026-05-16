package com.realfast.claims.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "claim_lines")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaimLine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int lineNumber;

    @Column(nullable = false)
    private String procedureCode;

    private String diagnosisCode;

    @Column(nullable = false)
    private LocalDate serviceDate;

    @Column(nullable = false)
    private int units;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal billedAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal approvedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private LineStatus status;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "line_adjustments", joinColumns = @JoinColumn(name = "claim_line_id"))
    private List<LineAdjustment> adjustments = new ArrayList<>();

    public ClaimLine(
            int lineNumber,
            String procedureCode,
            String diagnosisCode,
            LocalDate serviceDate,
            int units,
            BigDecimal billedAmount) {
        this.lineNumber = lineNumber;
        this.procedureCode = procedureCode;
        this.diagnosisCode = diagnosisCode;
        this.serviceDate = serviceDate;
        this.units = units;
        this.billedAmount = billedAmount;
        this.status = LineStatus.PENDING;
    }

    public void clearAdjudication() {
        this.approvedAmount = null;
        this.status = LineStatus.PENDING;
        this.adjustments.clear();
    }

    public void applyAdjudication(BigDecimal approvedAmount, LineStatus status, List<LineAdjustment> adjustments) {
        this.approvedAmount = approvedAmount;
        this.status = status;
        this.adjustments.clear();
        this.adjustments.addAll(adjustments);
    }

    public BigDecimal approvedAmountOrZero() {
        return approvedAmount != null ? approvedAmount : BigDecimal.ZERO;
    }

    /**
     * Derives line status from billed vs approved amounts after rules run.
     */
    public static LineStatus statusFromAmounts(BigDecimal billedAmount, BigDecimal approvedAmount) {
        if (approvedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return LineStatus.DENIED;
        }
        if (approvedAmount.compareTo(billedAmount) < 0) {
            return LineStatus.REDUCED;
        }
        return LineStatus.APPROVED;
    }
}
