package de.muenchen.captchaservice.service.difficulty;

import de.muenchen.captchaservice.configuration.captcha.CaptchaProperties;
import de.muenchen.captchaservice.configuration.captcha.CaptchaSite;
import de.muenchen.captchaservice.configuration.captcha.DifficultyItem;
import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.entity.CaptchaRequest;
import de.muenchen.captchaservice.repository.CaptchaRequestRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class DifficultyService {

    private final CaptchaProperties captchaProperties;

    private final CaptchaRequestRepository captchaRequestRepository;

    @SuppressFBWarnings(value = { "EI_EXPOSE_REP2" }, justification = "Dependency Injection")
    public DifficultyService(final CaptchaProperties captchaProperties, final CaptchaRequestRepository captchaRequestRepository) {
        this.captchaProperties = captchaProperties;
        this.captchaRequestRepository = captchaRequestRepository;
    }

    public void registerRequest(final String siteKey, final SourceAddress sourceAddress) {
        final String sourceAddressHash = sourceAddress.getHash();
        final CaptchaRequest captchaRequest = new CaptchaRequest(sourceAddressHash, isSourceAddressWhitelisted(siteKey, sourceAddress),
                Instant.now().plusSeconds(captchaProperties.sourceAddressWindowSeconds()));
        captchaRequestRepository.save(captchaRequest);
        log.debug("Registered request for source address with hash {}", sourceAddressHash);
    }

    public long getDifficultyForSourceAddress(final String siteKey, final SourceAddress sourceAddress) {
        if (isSourceAddressWhitelisted(siteKey, sourceAddress)) {
            return 1L;
        }
        final CaptchaSite captchaSite = captchaProperties.sites().get(siteKey);
        if (captchaSite == null) {
            throw new IllegalArgumentException("siteKey not found");
        }
        final String sourceAddressHash = sourceAddress.getHash();
        final long sourceVisitCount = captchaRequestRepository.countBySourceAddressHashIgnoreCaseAndExpiresAtGreaterThanEqual(sourceAddressHash,
                Instant.now());
        final Optional<DifficultyItem> difficultyItem = captchaSite
                .difficultyMap()
                .stream()
                .sorted((o1, o2) -> Math.toIntExact(o2.minVisits() - o1.minVisits()))
                .filter(di -> di.minVisits() - 1 <= sourceVisitCount)
                .findFirst();
        if (difficultyItem.isEmpty()) {
            log.error("No difficulty found site {} with {} visits", siteKey, sourceVisitCount);
            return 1_000_000L;
        }
        final long maxNumber = difficultyItem.get().maxNumber();
        log.debug("Difficulty {} for {} in {} after {} visits", maxNumber, sourceAddressHash, siteKey, sourceVisitCount);
        return maxNumber;
    }

    public boolean isSourceAddressWhitelisted(final String siteKey, final SourceAddress sourceAddress) {
        final CaptchaSite captchaSite = captchaProperties.sites().get(siteKey);
        for (String subnet : captchaSite.whitelistedSourceAddresses()) {
            if (new IpAddressMatcher(subnet).matches(sourceAddress.getSourceAddress())) return true;
        }
        return false;
    }

}
