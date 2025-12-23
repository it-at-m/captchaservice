package de.muenchen.captchaservice.entity;

import de.muenchen.captchaservice.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_invalidated_payload_payload_hash", columnList = "payloadHash"),
                @Index(name = "idx_invalidated_payload_payload_hash_expires_at", columnList = "payloadHash, expiresAt")
        }
)
// Definition of getter, setter, ...
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class InvalidatedPayload extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // ========= //
    // Variables //
    // ========= //

    @Column(nullable = false, length = 64)
    @NotNull @Size(min = 64, max = 64) private String payloadHash;

    @NotNull private Instant expiresAt;

}
