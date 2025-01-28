package de.muenchen.refarch.controller.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostChallengeRequest {

    private String clientIpAddress;

}
