package de.muenchen.captchaservice.configuration.captcha;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CaptchaSite(
        String siteKey,
        String secret,
        @Min(1) Integer maxVerifiesPerPayload,
        @NotNull List<DifficultyItem> difficultyMap,
        @Min(0) @Max(32) Integer sourceAddressIpv4NetSize,
        @Min(0) @Max(128) Integer sourceAddressIpv6NetSize,
        List<String> whitelistedSourceAddresses) {
    public CaptchaSite(
            final String siteKey,
            final String secret,
            final Integer maxVerifiesPerPayload,
            final List<DifficultyItem> difficultyMap,
            final Integer sourceAddressIpv4NetSize,
            final Integer sourceAddressIpv6NetSize,
            final List<String> whitelistedSourceAddresses) {
        this.siteKey = siteKey;
        this.secret = secret;
        this.maxVerifiesPerPayload = maxVerifiesPerPayload != null ? maxVerifiesPerPayload : 1;
        this.difficultyMap = List.copyOf(difficultyMap);
        this.sourceAddressIpv4NetSize = sourceAddressIpv4NetSize != null ? sourceAddressIpv4NetSize : 32;
        this.sourceAddressIpv6NetSize = sourceAddressIpv6NetSize != null ? sourceAddressIpv6NetSize : 128;
        this.whitelistedSourceAddresses = whitelistedSourceAddresses != null ? List.copyOf(whitelistedSourceAddresses) : List.of();
    }
}
