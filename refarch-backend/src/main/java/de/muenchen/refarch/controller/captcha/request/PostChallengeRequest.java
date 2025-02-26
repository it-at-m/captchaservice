package de.muenchen.refarch.controller.captcha.request;

import de.muenchen.refarch.validation.ValidSourceAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostChallengeRequest {

    @NotNull
    @NotBlank
    private String siteKey;

    @NotNull
    @NotBlank
    private String siteSecret;

    @NotNull
    @NotBlank
    @ValidSourceAddress
    private String clientAddress;

}
