package de.muenchen.captchaservice.repository;

import de.muenchen.captchaservice.entity.CaptchaRequest;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CaptchaRequestRepository extends PagingAndSortingRepository<CaptchaRequest, UUID>, CrudRepository<CaptchaRequest, UUID> {
    long countBySourceAddressHashIgnoreCaseAndExpiresAtGreaterThanEqual(String sourceAddressHash, Instant validUntil);

    long deleteByExpiresAtLessThan(Instant validUntil);

    int countBySourceAddressHashIgnoreCase(String sourceAddressHash);
}
