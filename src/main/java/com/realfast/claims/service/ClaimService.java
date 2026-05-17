package com.realfast.claims.service;

import com.realfast.claims.exception.ClaimNotFoundException;
import com.realfast.claims.model.Claim;
import com.realfast.claims.model.ClaimStatus;
import com.realfast.claims.repository.ClaimRepository;
import com.realfast.claims.web.dto.ClaimMapper;
import com.realfast.claims.web.dto.ClaimResponse;
import com.realfast.claims.web.dto.SubmitClaimRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final Adjudicator adjudicator;

    public ClaimService(ClaimRepository claimRepository, Adjudicator adjudicator) {
        this.claimRepository = claimRepository;
        this.adjudicator = adjudicator;
    }

    @Transactional
    public ClaimResponse submit(SubmitClaimRequest request) {
        Claim claim = ClaimMapper.toClaim(request);
        request.lines().forEach(lineRequest -> claim.addLine(ClaimMapper.toLine(lineRequest)));
        claim.recalculateTotals();
        return ClaimMapper.toResponse(claimRepository.save(claim));
    }

    @Transactional
    public ClaimResponse adjudicate(UUID id) {
        Claim claim = findClaimOrThrow(id);
        adjudicator.adjudicate(claim);
        return ClaimMapper.toResponse(claimRepository.save(claim));
    }

    @Transactional(readOnly = true)
    public ClaimResponse getById(UUID id) {
        return ClaimMapper.toResponse(findClaimOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getByStatus(ClaimStatus status) {
        return claimRepository.findByStatus(status).stream()
                .map(ClaimMapper::toResponse)
                .toList();
    }

    private Claim findClaimOrThrow(UUID id) {
        return claimRepository.findById(id).orElseThrow(() -> new ClaimNotFoundException(id));
    }
}
