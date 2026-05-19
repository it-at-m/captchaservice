package de.muenchen.captchaservice.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

@AllArgsConstructor
@Data
public class SourceAddress {

    private final String address;

    public String getHash() {
        return DigestUtils.sha256Hex(address);
    }
}
