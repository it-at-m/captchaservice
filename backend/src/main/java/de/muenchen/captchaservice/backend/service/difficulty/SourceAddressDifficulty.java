package de.muenchen.captchaservice.backend.service.difficulty;

/**
 * Snapshot of adaptive-difficulty inputs for a source address at a point in time.
 * Avoids repeating the same visit-count query for metrics tagging.
 */
public record SourceAddressDifficulty(int difficulty, boolean whitelisted, long visitCount) {
}
