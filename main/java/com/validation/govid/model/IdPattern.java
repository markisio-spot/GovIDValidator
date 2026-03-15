package com.validation.govid.model;

import java.util.regex.Pattern;

/**
 * Immutable descriptor that binds an {@link IdType} to its compiled regex {@link Pattern}
 * and a human-readable description of the expected format.
 *
 * <p>IdPattern instances are created once at application startup by
 * {@code CanadianIdPatterns} and {@code USIdPatterns}, then stored in the
 * {@link com.validation.govid.service.GovernmentIdValidatorService} registry.
 * They are never modified after construction, making them safe to share across threads.
 *
 * <p>All patterns are compiled with {@link Pattern#CASE_INSENSITIVE} so that
 * input normalisation (uppercasing) is a safety measure rather than a strict requirement.
 *
 * @param idType            the ID type this pattern applies to
 * @param pattern           compiled regular expression; anchored (^ ... $) to require full-string match
 * @param formatDescription plain-English description of the expected format, e.g.
 *                          {@code "2 letters followed by 6 digits (AB123456)"}
 *
 * @author Markisio
 * @version 1.0.0
 */
public record IdPattern(IdType idType, Pattern pattern, String formatDescription) {

    /**
     * Tests whether the given normalised value matches this pattern.
     *
     * <p>The value should already be trimmed and uppercased before calling this method;
     * normalisation is the responsibility of the calling service.
     *
     * @param normalizedValue the pre-normalised ID string to test
     * @return {@code true} if the value fully matches the pattern; {@code false} otherwise
     */
    public boolean matches(String normalizedValue) {
        return pattern.matcher(normalizedValue).matches();
    }
}
