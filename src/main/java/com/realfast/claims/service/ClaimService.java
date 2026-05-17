package com.realfast.claims.service;

import com.realfast.claims.exception.ClaimNotFoundException;
import com.realfast.claims.model.Claim;
import com.realfast.claims.model.ClaimLine;
import com.realfast.claims.model.ClaimStatus;
import com.realfast.claims.repository.ClaimRepository;
import com.realfast.claims.web.dto.ClaimMapper;
import com.realfast.claims.web.dto.ClaimResponse;
import com.realfast.claims.web.dto.SubmitClaimLineRequest;
import com.realfast.claims.web.dto.SubmitClaimRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final Adjudicator adjudicator;

    public ClaimService(ClaimRepository claimRepository, Adjudicator adjudicator) {
        this.claimRepository = claimRepository;
        this.adjudicator = adjudicator;
    }

    public ClaimResponse submit(SubmitClaimRequest request) {
        Claim claim = buildClaim(request);
        Claim saved = claimRepository.save(claim);
        return toResponse(saved);
    }

    public ClaimResponse adjudicate(UUID id) {
        Claim claim = loadClaim(id);
        adjudicator.adjudicate(claim);
        Claim saved = claimRepository.save(claim);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ClaimResponse getById(UUID id) {
        return toResponse(loadClaim(id));
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getByStatus(ClaimStatus status) {
        return claimRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .toList();
    }

    private Claim buildClaim(SubmitClaimRequest request) {
        Claim claim = ClaimMapper.toClaim(request);
        request.lines().forEach(lineRequest -> claim.addLine(toLine(lineRequest)));
        claim.recalculateTotals();
        return claim;
    }

    private ClaimLine toLine(SubmitClaimLineRequest lineRequest) {
        return ClaimMapper.toLine(lineRequest);
    }

    private ClaimResponse toResponse(Claim claim) {
        return ClaimMapper.toResponse(claim);
    }

    private Claim loadClaim(UUID id) {
        return claimRepository.findById(id).orElseThrow(() -> new ClaimNotFoundException(id));
    }
}
