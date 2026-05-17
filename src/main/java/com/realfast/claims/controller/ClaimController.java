package com.realfast.claims.controller;

import com.realfast.claims.model.ClaimStatus;
import com.realfast.claims.service.ClaimService;
import com.realfast.claims.web.dto.ClaimResponse;
import com.realfast.claims.web.dto.SubmitClaimRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClaimResponse submit(@Valid @RequestBody SubmitClaimRequest request) {
        return claimService.submit(request);
    }

    @PostMapping("/{id}/adjudicate")
    public ClaimResponse adjudicate(@PathVariable UUID id) {
        return claimService.adjudicate(id);
    }

    @GetMapping("/{id}")
    public ClaimResponse getById(@PathVariable UUID id) {
        return claimService.getById(id);
    }

    @GetMapping
    public List<ClaimResponse> listByStatus(@RequestParam ClaimStatus status) {
        return claimService.getByStatus(status);
    }
}
