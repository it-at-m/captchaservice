package de.muenchen.captchaservice.data;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

@AllArgsConstructor
public class SourceAddress {

    final private String sourceAddress;

    public String getHash() {
        return DigestUtils.sha256Hex(sourceAddress);
    }
}
