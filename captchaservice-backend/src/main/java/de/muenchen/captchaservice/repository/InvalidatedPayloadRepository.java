package de.muenchen.captchaservice.repository;

import de.muenchen.captchaservice.entity.InvalidatedPayload;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.Instant;
import java.util.UUID;

public interface InvalidatedPayloadRepository extends PagingAndSortingRepository<InvalidatedPayload, UUID>, CrudRepository<InvalidatedPayload, UUID> {
    long countByPayloadHashIgnoreCaseAndValidUntilGreaterThanEqual(String payloadHash, Instant validUntil);
}
