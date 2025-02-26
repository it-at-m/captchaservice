package de.muenchen.refarch.service.difficulty;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import de.muenchen.refarch.configuration.captcha.CaptchaProperties;
import de.muenchen.refarch.configuration.captcha.CaptchaSite;
import de.muenchen.refarch.configuration.captcha.DifficultyItem;
import de.muenchen.refarch.data.SourceAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DifficultyService {

    private final CaptchaProperties captchaProperties;
    private final IMap<String, String> sourceAddresses;

    public DifficultyService(final CaptchaProperties captchaProperties, final HazelcastInstance hazelcastInstance) {
        this.captchaProperties = captchaProperties;
        sourceAddresses = hazelcastInstance.getMap("SOURCE_ADDRESSES");
    }

    public void pokeSourceAddress(final SourceAddress sourceAddress) {
        final String sourceAddressHash = sourceAddress.getHash();
        sourceAddresses.set(String.format("%s_%s_%s", sourceAddressHash, System.currentTimeMillis(), UUID.randomUUID()), "",
                captchaProperties.sourceAddressWindowSeconds(), TimeUnit.SECONDS);
    }

    public long getDifficultyForSourceAddress(final String siteKey, final SourceAddress sourceAddress) {
        final Optional<CaptchaSite> captchaSiteOptional = captchaProperties.sites().stream().filter(site -> site.siteKey().equals(siteKey)).findFirst();
        if (captchaSiteOptional.isEmpty()) {
            throw new IllegalArgumentException("siteKey not found");
        }
        final CaptchaSite captchaSite = captchaSiteOptional.get();
        final String sourceAddressHash = sourceAddress.getHash();
        final long sourceVisitCount = sourceAddresses.keySet().stream().filter(s -> s.startsWith(String.format("%s_", sourceAddressHash))).count();
        final Optional<DifficultyItem> difficultyItem = captchaSite
                .difficultyMap()
                .stream()
                .sorted((o1, o2) -> Math.toIntExact(o2.minVisits() - o1.minVisits()))
                .filter(di -> di.minVisits() <= sourceVisitCount)
                .findFirst();
        if (difficultyItem.isEmpty()) {
            log.error("No difficulty found site {} with {} visits", siteKey, sourceVisitCount);
            return 1_000_000L;
        }
        final long maxNumber = difficultyItem.get().maxNumber();
        log.debug("Difficulty {} for {} in {} after {} visits", maxNumber, sourceAddressHash, siteKey, sourceVisitCount);
        return maxNumber;
    }

}
