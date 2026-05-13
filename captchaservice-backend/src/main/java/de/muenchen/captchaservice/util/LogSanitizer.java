package de.muenchen.captchaservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Neutralizes CR/LF in values written to logs to mitigate log injection (CWE-117).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogSanitizer {

    public static String sanitize(final String value) {
        if (value == null) {
            return null;
        }
        return value.replace('\r', '_').replace('\n', '_');
    }

    public static String sanitizeObject(final Object value) {
        if (value == null) {
            return "null";
        }
        return sanitize(String.valueOf(value));
    }
}
