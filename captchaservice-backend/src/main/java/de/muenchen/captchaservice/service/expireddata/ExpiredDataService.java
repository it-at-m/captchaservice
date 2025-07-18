package de.muenchen.captchaservice.service.expireddata;

import de.muenchen.captchaservice.repository.CaptchaRequestRepository;
import de.muenchen.captchaservice.repository.InvalidatedPayloadRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class ExpiredDataService {
    private final CaptchaRequestRepository captchaRequestRepository;
    private final InvalidatedPayloadRepository invalidatedPayloadRepository;

    @SuppressFBWarnings(value = { "EI_EXPOSE_REP2" }, justification = "Dependency Injection")
    public ExpiredDataService(final CaptchaRequestRepository captchaRequestRepository, final InvalidatedPayloadRepository invalidatedPayloadRepository) {
        this.captchaRequestRepository = captchaRequestRepository;
        this.invalidatedPayloadRepository = invalidatedPayloadRepository;
    }

    @Transactional
    public void deleteExpiredData() {
        Instant now = Instant.now();
        final long deletedCaptchaRequestCount = captchaRequestRepository.deleteByExpiresAtLessThan(now);
        if (deletedCaptchaRequestCount > 0) {
            log.info("Deleted {} expired CaptchaRequests", deletedCaptchaRequestCount);
        }
        final long deletedInvalidatedPayloadCount = invalidatedPayloadRepository.deleteByExpiresAtLessThan(now);
        if (deletedInvalidatedPayloadCount > 0) {
            log.info("Deleted {} expired InvalidatedPayloads", deletedInvalidatedPayloadCount);
        }
    }
}
