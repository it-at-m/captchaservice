package de.muenchen.captchaservice.entity;

import de.muenchen.captchaservice.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_captcha_request_source_address_hash", columnList = "sourceAddressHash"),
                @Index(name = "idx_captcha_request_valid_until", columnList = "validUntil")
        }
)

// Definition of getter, setter, ...
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // ========= //
    // Variables //
    // ========= //

    @Column(nullable = false, length = 64)
    @NotNull
    @Size(min = 64, max = 64)
    private String sourceAddressHash;

    @NotNull
    private Instant validUntil;

}
