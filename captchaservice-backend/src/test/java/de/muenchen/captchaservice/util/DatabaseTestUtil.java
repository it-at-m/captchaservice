package de.muenchen.captchaservice.util;

import de.muenchen.captchaservice.repository.InvalidatedPayloadRepository;
import de.muenchen.captchaservice.repository.CaptchaRequestRepository;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTestUtil {

    private final CaptchaRequestRepository captchaRequestRepository;
    private final InvalidatedPayloadRepository invalidatedPayloadRepository;

    public DatabaseTestUtil(CaptchaRequestRepository captchaRequestRepository, InvalidatedPayloadRepository invalidatedPayloadRepository) {
        this.captchaRequestRepository = captchaRequestRepository;
        this.invalidatedPayloadRepository = invalidatedPayloadRepository;
    }

    public void clearDatabase() {
        captchaRequestRepository.deleteAll();
        invalidatedPayloadRepository.deleteAll();
    }

}
