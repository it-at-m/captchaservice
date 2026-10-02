package de.muenchen.captchaservice.backend.repository;

import de.muenchen.captchaservice.backend.entity.CaptchaRequest;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CaptchaRequestRepository extends PagingAndSortingRepository<CaptchaRequest, UUID>, CrudRepository<CaptchaRequest, UUID> {
    long countBySourceAddressHashAndExpiresAtGreaterThanEqual(String sourceAddressHash, Instant validUntil);

    long deleteByExpiresAtLessThan(Instant validUntil);

    int countBySourceAddressHash(String sourceAddressHash);
}
