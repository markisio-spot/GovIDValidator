package com.validation.govid.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents the outcome of a single ID format validation call.
 *
 * <p>Every call to
 * {@link com.validation.govid.service.GovernmentIdValidatorService#validate(IdType, String)}
 * returns a {@code ValidationResult}. Callers should check {@link #isValid()} first,
 * then inspect {@link #getFailureReason()} when the result is invalid.
 *
 * <p>Instances are immutable and created exclusively via the static factory methods
 * {@link #pass} and {@link #fail} — never via the Lombok builder directly.
 *
 * <p>Example usage:
 * <pre>{@code
 * ValidationResult result = validator.validate(IdType.CA_SIN, "046-454-286");
 * if (!result.isValid()) {
 *     log.warn("Bad ID [{}]: {}", result.getValue(), result.getFailureReason());
 * }
 * }</pre>
 *
 * @author Markisio
 * @version 1.0.0
 */
@Getter
@Builder
public class ValidationResult {

    /** {@code true} if the value satisfies the format rules for the given {@link IdType}. */
    private final boolean valid;

    /** The {@link IdType} that was validated. */
    private final IdType idType;

    /**
     * The normalised (trimmed + uppercased) value that was tested against the pattern.
     * May differ from the raw input if the caller passed lower-case or padded input.
     */
    private final String value;

    /**
     * Human-readable description of the expected format for this ID type,
     * e.g. {@code "9 digits (###-###-###), first digit 1–9"}.
     * Always present, even when the result is invalid.
     */
    private final String formatDescription;

    /**
     * Explains why validation failed. {@code null} when {@link #isValid()} is {@code true}.
     * Examples:
     * <ul>
     *   <li>{@code "Does not match expected format: 2 letters followed by 6 digits"}</li>
     *   <li>{@code "Fails Luhn (mod-10) checksum"}</li>
     *   <li>{@code "Value is null or blank"}</li>
     * </ul>
     */
    private final String failureReason;

    // ── Static factory methods ────────────────────────────────────────────────

    /**
     * Creates a passing (valid) result.
     *
     * @param idType            the ID type that was validated
     * @param value             the normalised value that passed
     * @param formatDescription the expected format description for this ID type
     * @return a {@code ValidationResult} with {@code valid = true}
     */
    public static ValidationResult pass(IdType idType, String value, String formatDescription) {
        return ValidationResult.builder()
                .valid(true)
                .idType(idType)
                .value(value)
                .formatDescription(formatDescription)
                .build();
    }

    /**
     * Creates a failing (invalid) result.
     *
     * @param idType            the ID type that was validated
     * @param value             the normalised value that failed
     * @param formatDescription the expected format description for this ID type
     * @param reason            a specific, human-readable explanation of why validation failed
     * @return a {@code ValidationResult} with {@code valid = false}
     */
    public static ValidationResult fail(IdType idType, String value, String formatDescription, String reason) {
        return ValidationResult.builder()
                .valid(false)
                .idType(idType)
                .value(value)
                .formatDescription(formatDescription)
                .failureReason(reason)
                .build();
    }

    /**
     * Returns a concise string representation useful for logging.
     *
     * @return e.g. {@code "VALID [CA_SIN] \"046-454-286\" — 9 digits (###-###-###), first digit 1–9"}
     */
    @Override
    public String toString() {
        if (valid) {
            return String.format("VALID [%s] \"%s\" — %s", idType, value, formatDescription);
        }
        return String.format("INVALID [%s] \"%s\" — %s. Reason: %s", idType, value, formatDescription, failureReason);
    }
}
