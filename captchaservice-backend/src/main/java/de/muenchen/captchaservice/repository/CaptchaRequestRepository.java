package de.muenchen.captchaservice.repository;

import de.muenchen.captchaservice.entity.CaptchaRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.Instant;
import java.util.UUID;

public interface CaptchaRequestRepository extends PagingAndSortingRepository<CaptchaRequest, UUID>, CrudRepository<CaptchaRequest, UUID> {
    long countBySourceAddressHashIgnoreCaseAndValidUntilGreaterThanEqual(String sourceAddressHash, Instant validUntil);

    long deleteByValidUntilLessThan(Instant validUntil);

    int countBySourceAddressHash(String sourceAddressHash);
}
