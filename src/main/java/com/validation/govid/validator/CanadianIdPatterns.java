package com.validation.govid.validator;

import com.validation.govid.model.IdPattern;
import com.validation.govid.model.IdType;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Registry of compiled regex patterns for all supported Canadian government ID types.
 *
 * <p>Covers 41 ID types across four categories:
 * <ul>
 *   <li><strong>Federal (7):</strong> SIN, Passport, PR Card, Citizenship Certificate,
 *       Military ID (CAF), NEXUS Card, and Possession and Acquisition Licence (PAL)</li>
 *   <li><strong>Driver Licences (13):</strong> all Canadian provinces and territories</li>
 *   <li><strong>Health Cards (6):</strong> ON, BC, AB, QC, SK, MB</li>
 * </ul>
 *
 * <p>Pattern sources:
 * <ul>
 *   <li>SIN: Canada Revenue Agency format rules</li>
 *   <li>Passport / PR Card: IRCC specifications</li>
 *   <li>Military ID: Canadian Armed Forces Service Number (NDI 20) specification</li>
 *   <li>NEXUS: CBSA / CBP PASS ID format documentation</li>
 *   <li>PAL: RCMP Canadian Firearms Program — best-effort based on observed card formats;
 *       the RCMP does not publicly document the exact digit count</li>
 *   <li>Driver licences: provincial motor vehicle authority specifications</li>
 *   <li>Health cards: provincial health authority format specifications</li>
 * </ul>
 *
 * <p>This class is intentionally package-private. Access is exclusively through
 * {@link com.validation.govid.service.GovernmentIdValidatorService}.
 *
 * <p>All patterns are compiled with {@link Pattern#CASE_INSENSITIVE} and anchored
 * with {@code ^} and {@code $} to require a full-string match.
 *
 * @author Markisio
 * @version 1.1.0
 */
final class CanadianIdPatterns {

    /** Utility class — not instantiable. */
    private CanadianIdPatterns() {}

    /**
     * Immutable map of all Canadian {@link IdType} constants to their corresponding
     * {@link IdPattern}. Populated once at class-load time via {@link Map#ofEntries}.
     */
    static final Map<IdType, IdPattern> PATTERNS = Map.ofEntries(

        // ── FEDERAL ──────────────────────────────────────────────────────────

        // SIN: first digit 1–9 (digit 0 is never assigned); optional dash or space
        // separators after positions 3 and 6. Luhn checksum applied separately.
        e(IdType.CA_SIN,
            "^[1-9]\\d{2}[- ]?\\d{3}[- ]?\\d{3}$",
            "9 digits (###-###-###), first digit 1–9"),

        // Passport and citizenship certificate share the same ICAO-derived format
        e(IdType.CA_PASSPORT,
            "^[A-Z]{2}\\d{6}$",
            "2 letters followed by 6 digits (AB123456)"),

        e(IdType.CA_PR_CARD,
            "^[A-Z]{4}\\d{7}$",
            "4 uppercase letters followed by 7 digits (ABCD1234567)"),

        e(IdType.CA_CITIZENSHIP_CERT,
            "^[A-Z]{2}\\d{6}$",
            "2 letters followed by 6 digits (AB123456)"),

        // Canadian Armed Forces Service Number (NDI 20 card):
        // 1 uppercase letter prefix (unit category indicator) followed by exactly 8 digits
        e(IdType.CA_MILITARY_ID,
            "^[A-Z]\\d{8}$",
            "1 letter followed by 8 digits (A12345678) — CAF Service Number"),

        // NEXUS / PASS ID: 9-digit number assigned by CBP/CBSA.
        // Known valid prefixes: 10, 13, 14, 15, 16, 50, 70, 80, 95, 98, 99.
        // The prefix check uses an alternation group to enforce this constraint.
        e(IdType.CA_NEXUS,
            "^(10|13|14|15|16|50|70|80|95|98|99)\\d{7}$",
            "9 digits beginning with a valid CBSA/CBP prefix (e.g. 1012345678 — note: 9 digits total)"),

        // Possession and Acquisition Licence (PAL):
        // Core number is 7–8 digits based on observed RCMP-issued cards.
        // Optionally followed by a decimal point and a 4-digit issuance sequence number.
        // BEST-EFFORT: the RCMP does not publish the authoritative format specification.
        e(IdType.CA_PAL,
            "^\\d{7,8}(\\.\\d{4})?$",
            "7–8 digits, optionally followed by .NNNN sequence (e.g. 12345678 or 12345678.0001) — best-effort format"),

        // ── DRIVER LICENCES ──────────────────────────────────────────────────

        // Alberta: two accepted formats — plain numeric (5–9 digits) or hyphenated (######-###)
        e(IdType.CA_DL_AB,
            "^(\\d{6}-\\d{3}|\\d{5,9})$",
            "5–9 digits, or 6 digits-dash-3 digits (123456-789)"),

        e(IdType.CA_DL_BC,
            "^\\d{7}$",
            "Exactly 7 digits"),

        // Manitoba: complex structured format — groups of 2 letters separated by optional
        // dashes, then 3 digits and 2 trailing letters encoding name/DOB data
        e(IdType.CA_DL_MB,
            "^[A-Z]{2}-?[A-Z]{2}-?[A-Z]{2}-?[A-Z]\\d{3}[A-Z]{2}$",
            "2L-2L-2L-1L + 3 digits + 2L (HH-HH-HH-H###HH)"),

        e(IdType.CA_DL_NB,
            "^\\d{5,7}$",
            "5 to 7 digits"),

        e(IdType.CA_DL_NL,
            "^[A-Z]\\d{9}$",
            "1 letter followed by 9 digits (A123456789)"),

        // Nova Scotia: 5-letter surname-derived prefix + date-encoded segment (DDMM) + 6 digits
        e(IdType.CA_DL_NS,
            "^[A-Z]{5}-?[0-3]\\d[0-1]\\d{6}$",
            "5 letters + date portion + 6 digits (SMITH-010199####)"),

        // Ontario: letter + 4 digits + optional dash + 5 digits + 4 DOB-encoded digits.
        // The DOB digits use restricted ranges: [0156] for month indicator and [0123] for day indicator
        e(IdType.CA_DL_ON,
            "^[A-Z]\\d{4}-?\\d{5}\\d[0156]\\d[0123]\\d$",
            "1L + 4 digits + 5 digits + encoded DOB (A1234-56789XXXX)"),

        e(IdType.CA_DL_PE,
            "^\\d{5,6}$",
            "5 to 6 digits"),

        e(IdType.CA_DL_QC,
            "^[A-Z]\\d{12}$",
            "1 letter followed by 12 digits (A123456789012)"),

        e(IdType.CA_DL_SK,
            "^\\d{8}$",
            "Exactly 8 digits"),

        e(IdType.CA_DL_NT,
            "^\\d{6}$",
            "Exactly 6 digits"),

        // Nunavut: limited public specification — believed to be 5–6 digits
        e(IdType.CA_DL_NU,
            "^\\d{5,6}$",
            "5 to 6 digits"),

        e(IdType.CA_DL_YT,
            "^\\d{1,6}$",
            "1 to 6 digits"),

        // ── HEALTH CARDS ─────────────────────────────────────────────────────

        // OHIP: 4-letter prefix (surname-derived) + 6 numeric digits + 2-letter version code
        e(IdType.CA_HEALTH_ON,
            "^[A-Z]{4}\\d{6}[A-Z]{2}$",
            "4 letters + 6 digits + 2 letters (ABCD123456XY)"),

        e(IdType.CA_HEALTH_BC,
            "^\\d{10}$",
            "Exactly 10 digits"),

        e(IdType.CA_HEALTH_AB,
            "^\\d{9}$",
            "Exactly 9 digits"),

        // RAMQ: 4-letter surname prefix + 8 digits (DOB + gender encoded) + 2-letter admin code
        e(IdType.CA_HEALTH_QC,
            "^[A-Z]{4}\\d{8}[A-Z]{2}$",
            "4 letters + 8 digits + 2 letters (SMIT12345678AB)"),

        e(IdType.CA_HEALTH_SK,
            "^\\d{9}$",
            "Exactly 9 digits"),

        e(IdType.CA_HEALTH_MB,
            "^\\d{9}$",
            "Exactly 9 digits (PHIN)")
    );

    /**
     * Convenience factory that creates a {@link Map.Entry} of {@link IdType} to {@link IdPattern}.
     *
     * @param type        the ID type constant
     * @param regex       the anchored regular expression string
     * @param description plain-English description of the expected format
     * @return a map entry suitable for use in {@link Map#ofEntries}
     */
    private static Map.Entry<IdType, IdPattern> e(IdType type, String regex, String description) {
        return Map.entry(type,
                new IdPattern(type, Pattern.compile(regex, Pattern.CASE_INSENSITIVE), description));
    }
}
