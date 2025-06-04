package de.muenchen.captchaservice.util;

import de.muenchen.captchaservice.repository.CaptchaRequestRepository;
import de.muenchen.captchaservice.repository.InvalidatedPayloadRepository;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTestUtil {

    private final CaptchaRequestRepository captchaRequestRepository;
    private final InvalidatedPayloadRepository invalidatedPayloadRepository;

    public DatabaseTestUtil(final CaptchaRequestRepository captchaRequestRepository, final InvalidatedPayloadRepository invalidatedPayloadRepository) {
        this.captchaRequestRepository = captchaRequestRepository;
        this.invalidatedPayloadRepository = invalidatedPayloadRepository;
    }

    public void clearDatabase() {
        captchaRequestRepository.deleteAll();
        invalidatedPayloadRepository.deleteAll();
    }

}
