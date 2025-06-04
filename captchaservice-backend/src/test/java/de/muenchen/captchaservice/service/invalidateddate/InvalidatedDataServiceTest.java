package de.muenchen.captchaservice.service.invalidateddate;

import de.muenchen.captchaservice.TestConstants;
import de.muenchen.captchaservice.entity.CaptchaRequest;
import de.muenchen.captchaservice.entity.InvalidatedPayload;
import de.muenchen.captchaservice.repository.CaptchaRequestRepository;
import de.muenchen.captchaservice.repository.InvalidatedPayloadRepository;
import de.muenchen.captchaservice.util.DatabaseTestUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.time.Period;

import static de.muenchen.captchaservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.captchaservice.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class InvalidatedDataServiceTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));
    @Autowired
    private CaptchaRequestRepository captchaRequestRepository;
    @Autowired
    private DatabaseTestUtil databaseTestUtil;
    @Autowired
    private InvalidatedPayloadRepository invalidatedPayloadRepository;
    @Autowired
    private InvalidatedDataService invalidatedDataService;

    @Test
    void deleteInvalidatedData() {
        databaseTestUtil.clearDatabase();

        final String validHash = DigestUtils.sha256Hex("valid");
        final String invalidHash = DigestUtils.sha256Hex("invalid");

        // Invalid data
        captchaRequestRepository.save(new CaptchaRequest(validHash, Instant.now().plus(Period.ofDays(1))));
        captchaRequestRepository.save(new CaptchaRequest(validHash, Instant.now().plus(Period.ofDays(1))));
        invalidatedPayloadRepository.save(new InvalidatedPayload(validHash, Instant.now().plus(Period.ofDays(1))));
        invalidatedPayloadRepository.save(new InvalidatedPayload(validHash, Instant.now().plus(Period.ofDays(1))));

        // Valid data
        captchaRequestRepository.save(new CaptchaRequest(invalidHash, Instant.now().minus(Period.ofDays(1))));
        captchaRequestRepository.save(new CaptchaRequest(invalidHash, Instant.now().minus(Period.ofDays(1))));
        invalidatedPayloadRepository.save(new InvalidatedPayload(invalidHash, Instant.now().minus(Period.ofDays(1))));
        invalidatedPayloadRepository.save(new InvalidatedPayload(invalidHash, Instant.now().minus(Period.ofDays(1))));

        assertEquals(4, invalidatedPayloadRepository.count());
        assertEquals(4, captchaRequestRepository.count());

        invalidatedDataService.deleteInvalidatedData();

        assertEquals(2, invalidatedPayloadRepository.count());
        assertEquals(2, captchaRequestRepository.count());
        assertEquals(2, captchaRequestRepository.countBySourceAddressHash(validHash));
        assertEquals(2, invalidatedPayloadRepository.countByPayloadHash(validHash));
    }

}
