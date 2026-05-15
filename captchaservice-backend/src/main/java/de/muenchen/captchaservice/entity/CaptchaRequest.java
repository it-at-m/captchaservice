package de.muenchen.captchaservice.entity;

import de.muenchen.captchaservice.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_captcha_request_source_address_hash", columnList = "sourceAddressHash"),
                @Index(name = "idx_captcha_request_source_address_hash_expires_at", columnList = "sourceAddressHash, expiresAt")
        }
)

// Definition of getter, setter, ...
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class CaptchaRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // ========= //
    // Variables //
    // ========= //

    @CreationTimestamp
    @Column(updatable = false)
    private Instant requestAt;

    @Column(nullable = false, length = 64)
    @NotNull @Size(min = 64, max = 64) private String sourceAddressHash;

    @NotNull private boolean isWhitelisted;

    @NotNull private Instant expiresAt;

    public CaptchaRequest(String sourceAddressHash, boolean isWhitelisted, Instant expiresAt) {
        this.sourceAddressHash = sourceAddressHash;
        this.isWhitelisted = isWhitelisted;
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CaptchaRequest other = (CaptchaRequest) obj;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : System.identityHashCode(this);
    }

}
