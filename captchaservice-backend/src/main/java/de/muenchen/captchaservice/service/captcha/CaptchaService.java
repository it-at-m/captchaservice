package de.muenchen.captchaservice.service.captcha;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import de.muenchen.captchaservice.common.HazelcastConstants;
import de.muenchen.captchaservice.configuration.captcha.CaptchaProperties;
import de.muenchen.captchaservice.configuration.captcha.CaptchaSite;
import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.service.difficulty.DifficultyService;
import lombok.extern.slf4j.Slf4j;
import org.altcha.altcha.Altcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CaptchaService {

    private final CaptchaProperties captchaProperties;
    private final IMap<String, String> invalidatedPayloads;
    private final DifficultyService difficultyService;

    public CaptchaService(final CaptchaProperties captchaProperties, final DifficultyService difficultyService, final HazelcastInstance hazelcastInstance) {
        this.captchaProperties = captchaProperties;
        this.difficultyService = difficultyService;
        invalidatedPayloads = hazelcastInstance.getMap(HazelcastConstants.INVALIDATED_PAYLOADS);
    }

    public Altcha.Challenge createChallenge(final String siteKey, final SourceAddress sourceAddress) {
        final long difficulty = difficultyService.getDifficultyForSourceAddress(siteKey, sourceAddress);
        difficultyService.pokeSourceAddress(sourceAddress);
        final Altcha.ChallengeOptions options = new Altcha.ChallengeOptions();
        options.algorithm = Altcha.Algorithm.SHA256;
        options.hmacKey = captchaProperties.hmacKey();
        options.maxNumber = difficulty;
        options.expires = (System.currentTimeMillis() / 1000) + captchaProperties.captchaTimeoutSeconds();
        try {
            return Altcha.createChallenge(options);
        } catch (Exception e) {
            log.error("Error creating challenge: {}", e.getMessage());
        }
        return null;
    }

    public boolean verify(final String siteKey, final Altcha.Payload payload) {
        if (isPayloadInvalidated(siteKey, payload)) {
            return false;
        }
        try {
            final boolean isValid = Altcha.verifySolution(payload, captchaProperties.hmacKey(), true);
            if (isValid) {
                invalidatePayload(payload);
            }
            return isValid;
        } catch (Exception e) {
            log.warn("Error verifying captcha payload: {}", payload, e);
        }
        return false;
    }

    public void invalidatePayload(final Altcha.Payload payload) {
        final String payloadHash = getPayloadHash(payload);
        invalidatedPayloads.set(String.format("%s_%s_%s", payloadHash, System.currentTimeMillis(), UUID.randomUUID()), "",
                captchaProperties.captchaTimeoutSeconds(), TimeUnit.SECONDS);
        log.debug("Invalidated payloadHash: {}", payloadHash);
    }

    public boolean isPayloadInvalidated(final String siteKey, final Altcha.Payload payload) {
        final CaptchaSite site = captchaProperties.sites().get(siteKey);
        final String payloadHash = getPayloadHash(payload);
        final long payloadHashCount = invalidatedPayloads.keySet().stream().filter(s -> s.startsWith(String.format("%s_", payloadHash))).count();
        return payloadHashCount >= site.maxVerifiesPerPayload();
    }

    private static String getPayloadHash(final Altcha.Payload payload) {
        return DigestUtils.sha256Hex(String.format(
                "%s,%s,%d,%s,%s",
                payload.algorithm,
                payload.challenge,
                payload.number,
                payload.salt,
                payload.signature));
    }

}
