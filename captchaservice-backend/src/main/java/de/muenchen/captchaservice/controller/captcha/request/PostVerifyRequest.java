package de.muenchen.captchaservice.controller.captcha.request;

import de.muenchen.captchaservice.validation.ValidSourceAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import de.muenchen.captchaservice.data.ExtendedPayload;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVerifyRequest {

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

    @NotNull
    private ExtendedPayload payload;

}
