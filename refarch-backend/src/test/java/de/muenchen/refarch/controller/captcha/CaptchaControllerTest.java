package de.muenchen.refarch.controller.captcha;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.refarch.controller.captcha.request.PostChallengeRequest;
import de.muenchen.refarch.controller.captcha.request.PostVerifyRequest;
import de.muenchen.refarch.util.HazelcastTestUtil;
import lombok.SneakyThrows;
import org.altcha.altcha.Altcha;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static de.muenchen.refarch.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.refarch.TestConstants.SPRING_TEST_PROFILE;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@SuppressWarnings({ "PMD.AvoidUsingHardCodedIP", "PMD.AvoidDuplicateLiterals" })
class CaptchaControllerTest {

    private static final String TEST_SITE_KEY = "test_site";
    private static final String TEST_SITE_SECRET = "test_secret";
    private static final String TEST_HMAC_KEY = "secret";
    private static final Altcha.Payload TEST_PAYLOAD;

    static {
        TEST_PAYLOAD = new Altcha.Payload();
        TEST_PAYLOAD.algorithm = "";
        TEST_PAYLOAD.challenge = "";
        TEST_PAYLOAD.number = 1_000_000L;
        TEST_PAYLOAD.salt = "";
        TEST_PAYLOAD.signature = "";
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HazelcastTestUtil hazelcastTestUtil;

    @Test
    void postChallenge_basic() {
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

            final PostChallengeRequest request = new PostChallengeRequest(TEST_SITE_KEY, TEST_SITE_SECRET, "1.2.3.4");
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
    void postChallenge_validIpv4() {
        final PostChallengeRequest request = new PostChallengeRequest(TEST_SITE_KEY, TEST_SITE_SECRET, "1.2.3.4");
        final String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                post("/api/v1/captcha/challenge")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void postChallenge_validIpv6() {
        final PostChallengeRequest request = new PostChallengeRequest(TEST_SITE_KEY, TEST_SITE_SECRET, "2001:db8::");
        final String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                post("/api/v1/captcha/challenge")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void postChallenge_invalidSourceAddress() {
        final PostChallengeRequest request = new PostChallengeRequest(TEST_SITE_KEY, TEST_SITE_SECRET, "test");
        final String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                post("/api/v1/captcha/challenge")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void postVerify_success() {
        final PostVerifyRequest request = new PostVerifyRequest(TEST_SITE_KEY, TEST_SITE_SECRET, TEST_PAYLOAD);
        final String requestBody = objectMapper.writeValueAsString(request);
        try (MockedStatic<Altcha> mock = Mockito.mockStatic(Altcha.class)) {
            mock
                    .when(() -> Altcha.verifySolution(ArgumentMatchers.<Altcha.Payload>argThat(p -> p.algorithm.isEmpty()), eq(TEST_HMAC_KEY), eq(true)))
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
            mock.verify(() -> Altcha.verifySolution(ArgumentMatchers.<Altcha.Payload>argThat(p -> p.algorithm.isEmpty()), eq(TEST_HMAC_KEY), eq(true)));
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

    @Test
    @SneakyThrows
    void postVerify_expired() {
        hazelcastTestUtil.resetHazelcastInstance();
        final PostVerifyRequest request = new PostVerifyRequest(TEST_SITE_KEY, TEST_SITE_SECRET, TEST_PAYLOAD);
        final String requestBody = objectMapper.writeValueAsString(request);
        try (MockedStatic<Altcha> mock = Mockito.mockStatic(Altcha.class)) {
            // Successful request
            mock
                    .when(() -> Altcha.verifySolution(ArgumentMatchers.<Altcha.Payload>argThat(p -> p.algorithm.isEmpty()), eq(TEST_HMAC_KEY), eq(true)))
                    .thenReturn(true);
            mockMvc.perform(
                    post("/api/v1/captcha/verify")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.valid", is(true)));
            mock.verify(() -> Altcha.verifySolution(ArgumentMatchers.<Altcha.Payload>argThat(p -> p.algorithm.isEmpty()), eq(TEST_HMAC_KEY), eq(true)));

            // Expired request
            mock
                    .when(() -> Altcha.verifySolution(ArgumentMatchers.<Altcha.Payload>argThat(p -> p.algorithm.isEmpty()), eq(TEST_HMAC_KEY), eq(true)))
                    .thenReturn(true);
            mockMvc.perform(
                    post("/api/v1/captcha/verify")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.valid", is(false)));
            mock.verify(() -> Altcha.verifySolution(ArgumentMatchers.<Altcha.Payload>argThat(p -> p.algorithm.isEmpty()), eq(TEST_HMAC_KEY), eq(true)));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
