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

        @Min(0) @Max(32) Integer sourceAddressIpv4Cidr,
        @Min(0) @Max(128) Integer sourceAddressIpv6Cidr) {
    public CaptchaSite(
            final String siteKey,
            final String secret,
            final Integer maxVerifiesPerPayload,
            final List<DifficultyItem> difficultyMap,
            final Integer sourceAddressIpv4Cidr,
            final Integer sourceAddressIpv6Cidr) {
        this.siteKey = siteKey;
        this.secret = secret;
        this.maxVerifiesPerPayload = maxVerifiesPerPayload != null ? maxVerifiesPerPayload : 1;
        this.difficultyMap = List.copyOf(difficultyMap);
        this.sourceAddressIpv4Cidr = sourceAddressIpv4Cidr != null ? sourceAddressIpv4Cidr : 32;
        this.sourceAddressIpv6Cidr = sourceAddressIpv6Cidr != null ? sourceAddressIpv6Cidr : 128;
    }
}
