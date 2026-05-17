package com.realfast.claims.repository;

import com.realfast.claims.model.Claim;
import com.realfast.claims.model.ClaimStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence for {@link Claim} aggregates.
 */
public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    List<Claim> findByStatus(ClaimStatus status);
}
