package de.muenchen.captchaservice.repository;

import de.muenchen.captchaservice.entity.InvalidatedPayload;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InvalidatedPayloadRepository extends PagingAndSortingRepository<InvalidatedPayload, UUID>, CrudRepository<InvalidatedPayload, UUID> {
    long countByPayloadHashIgnoreCaseAndExpiresAtGreaterThanEqual(String payloadHash, Instant validUntil);

    long deleteByExpiresAtLessThan(Instant validUntil);

    long countByPayloadHash(String payloadHash);

    long countByExpiresAtGreaterThan(Instant now);
}
