package de.muenchen.refarch.controller.captcha;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.refarch.TestConstants;
import lombok.SneakyThrows;
import org.altcha.altcha.Altcha;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static de.muenchen.refarch.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.refarch.TestConstants.SPRING_TEST_PROFILE;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
class CaptchaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String TEST_HMAC_KEY = "secret";
    private static final String TEST_PAYLOAD = "payload";

    @Test
    void postChallenge() throws Exception {
        final Altcha.ChallengeOptions challengeOptions = new Altcha.ChallengeOptions();
        challengeOptions.algorithm = Altcha.Algorithm.SHA256;
        challengeOptions.hmacKey = TEST_HMAC_KEY;
        final Altcha.Challenge challenge = new Altcha.Challenge();
        challenge.algorithm = "SHA-256";
        challenge.challenge = "5808c72adc97dfdeb0146f0c24eac1d2073daf3ae1cdd7debbbed23462e7d03c";
        challenge.maxnumber = 1_000_000L;
        challenge.salt = "d065c7a20ad6e4920f567df4";
        challenge.signature = "0a706f95851b6a5803771d7c301a2b3ea9f8980ae4812c2e8ef80fa2ca081bfb";
        try (MockedStatic<Altcha> mock = Mockito.mockStatic(Altcha.class)) {
            mock
                    .when(() -> Altcha.createChallenge(
                            argThat(co -> Altcha.Algorithm.SHA256.equals(co.algorithm) &&
                                    TEST_HMAC_KEY.equals(co.hmacKey))))
                    .thenReturn(challenge);

            final PostChallengeRequest request = new PostChallengeRequest("1.2.3.4");
            final String requestBody = objectMapper.writeValueAsString(request);
            // --
            mockMvc.perform(
                    post("/api/v1/captcha/challenge")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.challenge.algorithm", is("SHA-256")))
                    .andExpect(jsonPath("$.challenge.challenge", is("5808c72adc97dfdeb0146f0c24eac1d2073daf3ae1cdd7debbbed23462e7d03c")))
                    .andExpect(jsonPath("$.challenge.maxnumber", is(1_000_000)))
                    .andExpect(jsonPath("$.challenge.salt", is("d065c7a20ad6e4920f567df4")))
                    .andExpect(jsonPath("$.challenge.signature", is("0a706f95851b6a5803771d7c301a2b3ea9f8980ae4812c2e8ef80fa2ca081bfb")));
            // --
            mock.verify(() -> Altcha.createChallenge(
                    argThat(co -> Altcha.Algorithm.SHA256.equals(co.algorithm) &&
                            TEST_HMAC_KEY.equals(co.hmacKey))));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SneakyThrows
    void postVerify_success() {
        final PostVerifyRequest request = new PostVerifyRequest("test_site", "test_secret", TEST_PAYLOAD);
        final String requestBody = objectMapper.writeValueAsString(request);
        try (MockedStatic<Altcha> mock = Mockito.mockStatic(Altcha.class)) {
            mock
                    .when(() -> Altcha.verifySolution(TEST_PAYLOAD, TEST_HMAC_KEY, true))
                    .thenReturn(true);
            // --
            mockMvc.perform(
                    post("/api/v1/captcha/verify")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.valid", is(true)));
            // --
            mock.verify(() -> Altcha.verifySolution(TEST_PAYLOAD, TEST_HMAC_KEY, true));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SneakyThrows
    void postVerify_unauthorized() {
        final PostVerifyRequest request = new PostVerifyRequest("test_site1", "test_secret1", TEST_PAYLOAD);
        final String requestBody = objectMapper.writeValueAsString(request);
        // --
        mockMvc.perform(
                post("/api/v1/captcha/verify")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
