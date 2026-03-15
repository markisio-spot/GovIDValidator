package com.validation.govid.service;

import com.validation.govid.exception.InvalidIdTypeException;
import com.validation.govid.model.IdPattern;
import com.validation.govid.model.IdType;
import com.validation.govid.model.ValidationResult;
import com.validation.govid.validator.CanadianIdPatterns;
import com.validation.govid.validator.SinLuhnValidator;
import com.validation.govid.validator.USIdPatterns;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core validation service for government ID format checking.
 *
 * <p>This is the single public-facing component of the {@code gov-id-validator} library.
 * It is registered as a Spring {@code @Service} bean and auto-configured for injection
 * into any Spring Boot application that declares the library as a dependency.
 *
 * <p><strong>Thread safety:</strong> This service is stateless after construction.
 * The internal registry is an unmodifiable map populated once at startup. It is safe
 * to use as a shared singleton across multiple threads with no synchronisation.
 *
 * <p><strong>What this service validates:</strong>
 * <ul>
 *   <li>Structure — correct length, character types, and ordering of letters and digits</li>
 *   <li>Checksum — Luhn mod-10 for {@link IdType#CA_SIN} only</li>
 * </ul>
 *
 * <p><strong>What this service does NOT validate:</strong>
 * <ul>
 *   <li>Whether the ID has been issued to a real person</li>
 *   <li>Whether the ID is expired or revoked</li>
 *   <li>Whether the ID belongs to the person presenting it</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * @Autowired
 * private GovernmentIdValidatorService idValidator;
 *
 * // Validate by enum constant
 * ValidationResult r = idValidator.validate(IdType.CA_SIN, "046-454-286");
 * r.isValid();               // true
 * r.getFormatDescription();  // "9 digits (###-###-###), first digit 1–9"
 *
 * // Validate by string name — useful when idType comes from a form or REST param
 * ValidationResult r2 = idValidator.validate("US_DL_FL", "A123456789012");
 *
 * // Get the expected format without validating — useful for UI hints
 * String hint = idValidator.describeFormat(IdType.CA_PASSPORT);
 * }</pre>
 *
 * @author Markisio
 * @version 1.0.0
 * @see IdType
 * @see ValidationResult
 * @see InvalidIdTypeException
 */
@Service
public class GovernmentIdValidatorService {

    /**
     * Unified pattern registry containing all Canadian and US ID patterns.
     * Built once at construction time by merging {@code CanadianIdPatterns.PATTERNS}
     * and {@code USIdPatterns.PATTERNS}. Immutable after construction.
     */
    private final Map<IdType, IdPattern> registry;

    /**
     * Constructs the service and initialises the pattern registry.
     *
     * <p>Both the Canadian and US pattern maps are merged into a single {@link HashMap}.
     * All 95 regex patterns are already compiled at this point (they were compiled
     * as static constants in the pattern classes), so this constructor is lightweight.
     */
    public GovernmentIdValidatorService() {
        registry = new HashMap<>();
        registry.putAll(CanadianIdPatterns.PATTERNS);
        registry.putAll(USIdPatterns.PATTERNS);
    }

    /**
     * Validates the format of a government ID value against the rules for the given {@link IdType}.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Null / blank guard — returns an invalid result immediately if the value is null or blank.</li>
     *   <li>Normalisation — trims leading/trailing whitespace and uppercases the value.</li>
     *   <li>Pattern match — tests the normalised value against the registered regex.</li>
     *   <li>Luhn checksum — applied as a second pass only when {@code idType == CA_SIN}.</li>
     * </ol>
     *
     * @param idType   the type of government ID to validate against; must not be {@code null}
     * @param rawValue the raw user-entered ID string; surrounding whitespace is trimmed automatically
     * @return a {@link ValidationResult} — never {@code null}
     * @throws InvalidIdTypeException if {@code idType} is {@code null} or has no registered pattern
     */
    public ValidationResult validate(IdType idType, String rawValue) {
        if (idType == null) {
            throw new InvalidIdTypeException("IdType must not be null");
        }

        IdPattern pattern = registry.get(idType);
        if (pattern == null) {
            throw new InvalidIdTypeException("No pattern registered for IdType: " + idType);
        }

        // Guard against null / blank input before attempting any regex operation
        if (rawValue == null || rawValue.isBlank()) {
            return ValidationResult.fail(idType, rawValue, pattern.formatDescription(), "Value is null or blank");
        }

        // Normalise: trim surrounding whitespace, uppercase for consistent letter matching
        String normalized = normalize(rawValue);

        // Primary check: does the value match the expected structural pattern?
        if (!pattern.matches(normalized)) {
            return ValidationResult.fail(idType, normalized, pattern.formatDescription(),
                    "Does not match expected format: " + pattern.formatDescription());
        }

        // Secondary check (CA_SIN only): Luhn mod-10 checksum.
        // Strip separators before passing to the validator — it expects pure digits.
        if (idType == IdType.CA_SIN) {
            String digitsOnly = normalized.replaceAll("[- ]", "");
            if (!SinLuhnValidator.isValid(digitsOnly)) {
                return ValidationResult.fail(idType, normalized, pattern.formatDescription(),
                        "Fails Luhn (mod-10) checksum");
            }
        }

        return ValidationResult.pass(idType, normalized, pattern.formatDescription());
    }

    /**
     * Validates the format of a government ID value using an {@link IdType} name string.
     *
     * <p>This overload is convenient when the ID type is received as a string from a
     * REST request parameter, form input, or configuration — allowing validation without
     * requiring the caller to perform the enum lookup themselves.
     *
     * <p>The lookup is case-insensitive: {@code "ca_sin"} and {@code "CA_SIN"} both work.
     *
     * @param idTypeName the name of the {@link IdType} enum constant (case-insensitive),
     *                   e.g. {@code "CA_SIN"}, {@code "US_DL_FL"}
     * @param rawValue   the raw ID string to validate
     * @return a {@link ValidationResult} — never {@code null}
     * @throws InvalidIdTypeException if {@code idTypeName} does not match any known {@link IdType}
     */
    public ValidationResult validate(String idTypeName, String rawValue) {
        try {
            IdType idType = IdType.valueOf(idTypeName.toUpperCase());
            return validate(idType, rawValue);
        } catch (IllegalArgumentException e) {
            throw new InvalidIdTypeException("Unknown IdType: " + idTypeName);
        }
    }

    /**
     * Returns the human-readable format description for the given {@link IdType}
     * without performing any validation.
     *
     * <p>Useful for populating UI hints or tooltip text at the point of data entry.
     *
     * @param idType the ID type to describe; must not be {@code null}
     * @return a format description string, e.g. {@code "2 letters followed by 6 digits (AB123456)"}
     * @throws InvalidIdTypeException if {@code idType} has no registered pattern
     */
    public String describeFormat(IdType idType) {
        IdPattern pattern = registry.get(idType);
        if (pattern == null) {
            throw new InvalidIdTypeException("No pattern registered for IdType: " + idType);
        }
        return pattern.formatDescription();
    }

    /**
     * Returns a list of all {@link IdType} constants that have a registered pattern.
     *
     * <p>The returned list is a snapshot — modifications to it do not affect the registry.
     * Order is not guaranteed.
     *
     * @return a mutable list of all supported {@link IdType} values (currently 95)
     */
    public List<IdType> listAll() {
        return new ArrayList<>(registry.keySet());
    }

    /**
     * Returns {@code true} if the given {@link IdType} has a registered pattern
     * and can be used as an argument to {@link #validate(IdType, String)}.
     *
     * @param idType the ID type to check
     * @return {@code true} if supported; {@code false} if {@code idType} is {@code null}
     *         or not in the registry
     */
    public boolean supports(IdType idType) {
        return registry.containsKey(idType);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Normalises a raw input string for consistent pattern matching.
     *
     * <p>Trims leading and trailing whitespace, then converts all letters to uppercase.
     * This allows callers to pass values like {@code "ab123456"} or {@code " AB123456 "}
     * and have them treated identically.
     *
     * @param raw the raw input string; must not be {@code null}
     * @return the trimmed, uppercased value
     */
    private String normalize(String raw) {
        return raw.trim().toUpperCase();
    }
}
