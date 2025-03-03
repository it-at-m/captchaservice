package de.muenchen.captchaservice.data;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

@AllArgsConstructor
public class SourceAddress {

    final private InetAddress inetAddress;

    public static SourceAddress parse(final String sourceAddress) throws UnknownHostException {
        return new SourceAddress(InetAddress.getByName(sourceAddress));
    }

    public String getHash() {
        return DigestUtils.sha256Hex(inetAddress.toString());
    }
}
