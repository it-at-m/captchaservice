package de.muenchen.refarch.controller.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVerifyRequest {

    private String siteKey;
    private String siteSecret;
    private String payload;

}
