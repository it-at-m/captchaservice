package de.muenchen.captchaservice.service.sourceaddress;

import com.google.common.net.InetAddresses;
import de.muenchen.captchaservice.configuration.captcha.CaptchaProperties;
import de.muenchen.captchaservice.configuration.captcha.CaptchaSite;
import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.util.networkaddresscalculator.NetworkAddressCalculator;
import java.net.InetAddress;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SourceAddressService {
    private final CaptchaProperties captchaProperties;

    public SourceAddress parse(final String siteKey, final String sourceAddress) {
        CaptchaSite site = captchaProperties.sites().get(siteKey);
        String networkAddressString;
        InetAddress addr = InetAddresses.forString(sourceAddress);
        if (addr instanceof java.net.Inet4Address) {
            networkAddressString = NetworkAddressCalculator.getNetworkAddress(sourceAddress, site.sourceAddressIpv4NetSize());
        } else if (addr instanceof java.net.Inet6Address) {
            networkAddressString = NetworkAddressCalculator.getNetworkAddress(sourceAddress, site.sourceAddressIpv6NetSize());
        } else {
            throw new IllegalArgumentException("Unsupported IP address type: " + addr.getClass().getName());
        }
        return new SourceAddress(networkAddressString);
    }

}
