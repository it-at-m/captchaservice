package de.muenchen.captchaservice.backend.configuration.filter;

import static de.muenchen.captchaservice.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.captchaservice.backend.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.captchaservice.backend.CaptchaServiceApplication;
import de.muenchen.captchaservice.backend.TestConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        classes = { CaptchaServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureRestTestClient
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class CacheControlFilterTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String ENTITY_ENDPOINT_URL = "/actuator/metrics";

    private static final String EXPECTED_CACHE_CONTROL_HEADER_VALUES = "no-cache, no-store, must-revalidate";

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void givenActuatorMetricsEndpoint_thenCacheControlHeadersPresent() {
        restTestClient.get()
                .uri(ENTITY_ENDPOINT_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(HttpHeaders.CACHE_CONTROL)
                .expectHeader().valueEquals(HttpHeaders.CACHE_CONTROL, EXPECTED_CACHE_CONTROL_HEADER_VALUES);
    }

}
