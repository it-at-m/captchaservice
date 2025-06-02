package de.muenchen.captchaservice.service.difficulty;

import de.muenchen.captchaservice.TestConstants;
import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.util.HazelcastTestUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static de.muenchen.captchaservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.captchaservice.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.*;

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
    private HazelcastTestUtil hazelcastTestUtil;

    @Test
    @SneakyThrows
    void testDifficultyIncrease() {
        hazelcastTestUtil.resetHazelcastInstance();
        final SourceAddress sourceAddress = SourceAddress.parse("1.2.3.4");
        long difficulty;
        // --
        difficultyService.pokeSourceAddress(sourceAddress);
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(1_000L, difficulty);
        // --
        difficultyService.pokeSourceAddress(sourceAddress);
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(2_000L, difficulty);
        // --
        difficultyService.pokeSourceAddress(sourceAddress);
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(3_000L, difficulty);
        // --
        for (int i = 0; i < 5; i++) {
            difficultyService.pokeSourceAddress(sourceAddress);
        }
        difficulty = difficultyService.getDifficultyForSourceAddress(TEST_SITE_KEY, sourceAddress);
        assertEquals(3_000L, difficulty);
    }

}
