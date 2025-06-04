package de.muenchen.captchaservice.service.sourceaddress;

import de.muenchen.captchaservice.configuration.captcha.CaptchaProperties;
import de.muenchen.captchaservice.configuration.captcha.CaptchaSite;
import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.util.networkaddresscalculator.NetworkAddressCalculator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
@AllArgsConstructor
public class SourceAddressService {
    private final CaptchaProperties captchaProperties;

    public SourceAddress parse(final String siteKey, final String sourceAddress) {
        CaptchaSite site = captchaProperties.sites().get(siteKey);
        String networkAddressString;
        try {
            InetAddress addr = InetAddress.getByName(sourceAddress);
            if (addr instanceof java.net.Inet4Address) {
                networkAddressString = NetworkAddressCalculator.getNetworkAddress(sourceAddress, site.sourceAddressIpv4Cidr());
            } else if (addr instanceof java.net.Inet6Address) {
                networkAddressString = NetworkAddressCalculator.getNetworkAddress(sourceAddress, site.sourceAddressIpv6Cidr());
            } else {
                throw new IllegalArgumentException("Unsupported IP address type: " + addr.getClass().getName());
            }
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address provided: " + sourceAddress, e);
        }
        return new SourceAddress(networkAddressString);
    }

}
