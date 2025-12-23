package de.muenchen.captchaservice.service.difficulty;

import static de.muenchen.captchaservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.captchaservice.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muenchen.captchaservice.TestConstants;
import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.util.DatabaseTestUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
class DifficultyServiceTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String TEST_SITE_KEY = "test_site";

    @Autowired
    private DifficultyService difficultyService;

    @Autowired
    private DatabaseTestUtil databaseTestUtil;

    @Test
    @SneakyThrows
    void testDifficultyIncrease() {
        databaseTestUtil.clearDatabase();
        final SourceAddress sourceAddress = new SourceAddress("1.2.3.4");
        long difficulty;
        // --
        difficultyService.registerRequest("test_site", sourceAddress);
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(1_000L, difficulty);
        // --
        difficultyService.registerRequest("test_site", sourceAddress);
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(2_000L, difficulty);
        // --
        difficultyService.registerRequest("test_site", sourceAddress);
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(3_000L, difficulty);
        // --
        for (int i = 0; i < 5; i++) {
            difficultyService.registerRequest("test_site", sourceAddress);
        }
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(3_000L, difficulty);
    }

    @Test
    @SneakyThrows
    void testDifficultyIncreaseWithWhitelistedSourceAddress() {
        databaseTestUtil.clearDatabase();
        final SourceAddress sourceAddress = new SourceAddress("10.1.2.3");
        long difficulty;
        // --
        difficultyService.registerRequest("test_site", sourceAddress);
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(1L, difficulty);
        // --
        difficultyService.registerRequest("test_site", sourceAddress);
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(1L, difficulty);
        // --
        difficultyService.registerRequest("test_site", sourceAddress);
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(1L, difficulty);
        // --
        for (int i = 0; i < 5; i++) {
            difficultyService.registerRequest("test_site", sourceAddress);
        }
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(1L, difficulty);
    }

}
