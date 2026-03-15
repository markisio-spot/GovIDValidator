package com.validation.govid.validator;

import com.validation.govid.model.IdPattern;
import com.validation.govid.model.IdType;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Registry of compiled regex patterns for all supported United States government ID types.
 *
 * <p>Covers 57 ID types across two categories:
 * <ul>
 *   <li><strong>Federal (7):</strong> SSN, Passport, Passport Card, ITIN, EAD, Green Card, Military ID (CAC/EDIPI)</li>
 *   <li><strong>Driver Licences (51):</strong> all 50 states plus Washington D.C.</li>
 * </ul>
 *
 * <p>Pattern sources:
 * <ul>
 *   <li>SSN/ITIN: Social Security Administration and IRS format specifications</li>
 *   <li>Passport/Passport Card: US Department of State specifications</li>
 *   <li>EAD/Green Card: USCIS document format specifications</li>
 *   <li>Driver licences: AAMVA DL/ID Card Design Standard and state DMV specifications</li>
 * </ul>
 *
 * <p>This class is intentionally package-private. Access is exclusively through
 * {@link com.validation.govid.service.GovernmentIdValidatorService}, which merges
 * this registry with {@link CanadianIdPatterns} at construction time.
 *
 * <p>All patterns are compiled with {@link Pattern#CASE_INSENSITIVE} and anchored
 * with {@code ^} and {@code $} to require a full-string match.
 *
 * @author Markisio
 * @version 1.1.0
 */
final class USIdPatterns {

    /** Utility class — not instantiable. */
    private USIdPatterns() {}

    /**
     * Immutable map of all US {@link IdType} constants to their corresponding
     * {@link IdPattern}. Populated once at class-load time via {@link Map#ofEntries}.
     */
    static final Map<IdType, IdPattern> PATTERNS = Map.ofEntries(

        // ── FEDERAL ──────────────────────────────────────────────────────────

        // SSN: negative lookaheads exclude known-invalid area codes (000, 666, 900–999),
        // group 00, and serial 0000 — none of which the SSA has ever assigned.
        // Optional dashes or spaces between the three segments are accepted.
        e(IdType.US_SSN,
            "^(?!000|666|9\\d{2})\\d{3}[- ]?(?!00)\\d{2}[- ]?(?!0000)\\d{4}$",
            "9 digits (###-##-####), area ≠ 000/666/900–999"),

        e(IdType.US_PASSPORT,
            "^\\d{9}$",
            "Exactly 9 digits"),

        // Passport Card: always prefixed with letter C (US-specific document indicator)
        e(IdType.US_PASSPORT_CARD,
            "^C[A-Z0-9]{8}$",
            "Letter C followed by 8 alphanumeric characters (C12345678)"),

        // ITIN: structured like an SSN but area always starts with 9,
        // and group is restricted to 70–88, 90–92, or 94–99 (IRS-assigned ranges)
        e(IdType.US_ITIN,
            "^9\\d{2}[- ]?(?:7[0-9]|8[0-8]|9[0-24-9])[- ]?\\d{4}$",
            "9XX-##-#### where area starts with 9, group 70–99"),

        e(IdType.US_EAD,
            "^[A-Z]{3}\\d{10}$",
            "3 letters followed by 10 digits (ABC1234567890)"),

        e(IdType.US_GREEN_CARD,
            "^[A-Z]{3}\\d{9}$",
            "3 letters followed by 9 digits (ABC123456789)"),

        // US Military Common Access Card (CAC) — DoD EDIPI (Electronic Data Interchange
        // Personal Identifier). A 10-digit number permanently assigned to the cardholder
        // by the Department of Defense. Used on CAC cards for military, civilian, and
        // contractor personnel.
        e(IdType.US_MILITARY_ID,
            "^\\d{10}$",
            "Exactly 10 digits — DoD EDIPI number (e.g. 1234567890)"),

        // ── DRIVER LICENCES — All 50 States + DC ─────────────────────────────
        // Formats sourced from AAMVA DL/ID Card Design Standard and state DMV specs.

        e(IdType.US_DL_AL, "^\\d{1,7}$",                        "1–7 digits"),
        e(IdType.US_DL_AK, "^\\d{1,7}$",                        "1–7 digits"),
        // Arizona accepts either a letter-prefixed or all-numeric format
        e(IdType.US_DL_AZ, "^[A-Z]\\d{8}$|^\\d{9}$",           "1 letter + 8 digits, or 9 digits"),
        e(IdType.US_DL_AR, "^\\d{4,9}$",                        "4–9 digits"),
        e(IdType.US_DL_CA, "^[A-Z]\\d{7}$",                     "1 letter + 7 digits"),
        // Colorado has issued both numeric-only and alphanumeric formats over the years
        e(IdType.US_DL_CO, "^\\d{9}$|^[A-Z]{1,2}\\d{3,6}$",    "9 digits, or 1–2 letters + 3–6 digits"),
        e(IdType.US_DL_CT, "^\\d{9}$",                          "Exactly 9 digits"),
        e(IdType.US_DL_DE, "^\\d{1,7}$",                        "1–7 digits"),
        e(IdType.US_DL_FL, "^[A-Z]\\d{12}$",                    "1 letter + 12 digits"),
        e(IdType.US_DL_GA, "^\\d{7,9}$",                        "7–9 digits"),
        // Hawaii: newer licences use numeric-only; older licences prefixed with H
        e(IdType.US_DL_HI, "^H\\d{8}$|^\\d{9}$",               "H + 8 digits, or 9 digits"),
        // Idaho: unique format with a letter suffix
        e(IdType.US_DL_ID, "^[A-Z]{2}\\d{6}[A-Z]$",            "2 letters + 6 digits + 1 letter"),
        e(IdType.US_DL_IL, "^[A-Z]\\d{11,12}$",                 "1 letter + 11–12 digits"),
        e(IdType.US_DL_IN, "^\\d{10}$|^[A-Z]\\d{9}$",          "10 digits, or 1 letter + 9 digits"),
        // Iowa: some licences include a 2-letter suffix
        e(IdType.US_DL_IA, "^\\d{9}[A-Z]{2}$|^\\d{9}$",        "9 digits, or 9 digits + 2 letters"),
        e(IdType.US_DL_KS, "^[A-Z]\\d{8}$|^\\d{9}$",           "1 letter + 8 digits, or 9 digits"),
        e(IdType.US_DL_KY, "^[A-Z]\\d{8,9}$",                   "1 letter + 8–9 digits"),
        e(IdType.US_DL_LA, "^\\d{1,9}$",                        "1–9 digits"),
        e(IdType.US_DL_ME, "^\\d{7}$|^\\d{7}[A-Z]$",           "7 digits, or 7 digits + 1 letter"),
        e(IdType.US_DL_MD, "^[A-Z]\\d{12}$",                    "1 letter + 12 digits"),
        // Massachusetts: newer licences use S prefix; older are all-numeric
        e(IdType.US_DL_MA, "^S\\d{8}$|^\\d{9}$",               "S + 8 digits, or 9 digits"),
        e(IdType.US_DL_MI, "^[A-Z]\\d{12}$",                    "1 letter + 12 digits"),
        e(IdType.US_DL_MN, "^[A-Z]\\d{12}$",                    "1 letter + 12 digits"),
        e(IdType.US_DL_MS, "^\\d{9}$",                          "Exactly 9 digits"),
        e(IdType.US_DL_MO, "^[A-Z]\\d{5,9}$|^\\d{9}$",         "1 letter + 5–9 digits, or 9 digits"),
        // Montana: 13-digit format for standard; 9-digit (1L+8d) for older licences
        e(IdType.US_DL_MT, "^\\d{13}$|^[A-Z]\\d{8}$",          "13 digits, or 1 letter + 8 digits"),
        e(IdType.US_DL_NE, "^[A-Z]\\d{6,8}$",                   "1 letter + 6–8 digits"),
        // Nevada: three distinct format generations in use
        e(IdType.US_DL_NV, "^\\d{9,10}$|^\\d{12}$|^X\\d{8}$",  "9–10 digits, 12 digits, or X + 8 digits"),
        // New Hampshire: unique fixed-pattern mixing digits and letters
        e(IdType.US_DL_NH, "^\\d{2}[A-Z]{3}\\d{5}$",           "2 digits + 3 letters + 5 digits"),
        e(IdType.US_DL_NJ, "^[A-Z]\\d{14}$",                    "1 letter + 14 digits"),
        e(IdType.US_DL_NM, "^\\d{8,9}$",                        "8–9 digits"),
        // New York: wide range to accommodate enhanced, standard, and non-driver ID formats
        e(IdType.US_DL_NY, "^[A-Z]\\d{7,18}$|^\\d{8,9}$",      "1 letter + 7–18 digits, or 8–9 digits"),
        e(IdType.US_DL_NC, "^\\d{1,12}$",                       "1–12 digits"),
        e(IdType.US_DL_ND, "^[A-Z]{3}\\d{6}$|^\\d{9}$",        "3 letters + 6 digits, or 9 digits"),
        e(IdType.US_DL_OH, "^[A-Z]{2}\\d{6}$|^[A-Z]\\d{8}$",   "2 letters + 6 digits, or 1 letter + 8 digits"),
        e(IdType.US_DL_OK, "^[A-Z]\\d{9}$|^\\d{9}$",           "1 letter + 9 digits, or 9 digits"),
        e(IdType.US_DL_OR, "^\\d{1,9}$",                        "1–9 digits"),
        e(IdType.US_DL_PA, "^\\d{8}$",                          "Exactly 8 digits"),
        // Rhode Island: legacy licences prefixed with V
        e(IdType.US_DL_RI, "^\\d{7}$|^V\\d{6}$",               "7 digits, or V + 6 digits"),
        e(IdType.US_DL_SC, "^\\d{5,11}$",                       "5–11 digits"),
        e(IdType.US_DL_SD, "^\\d{6,10}$",                       "6–10 digits"),
        e(IdType.US_DL_TN, "^\\d{7,9}$",                        "7–9 digits"),
        e(IdType.US_DL_TX, "^\\d{7,8}$",                        "7–8 digits"),
        e(IdType.US_DL_UT, "^\\d{4,10}$",                       "4–10 digits"),
        e(IdType.US_DL_VT, "^\\d{8}$|^\\d{7}[A-Z]$",           "8 digits, or 7 digits + 1 letter"),
        e(IdType.US_DL_VA, "^[A-Z]\\d{8,11}$|^\\d{9}$",        "1 letter + 8–11 digits, or 9 digits"),
        // Washington: variable-length alphanumeric based on name encoding
        e(IdType.US_DL_WA, "^[A-Z]{1,7}[A-Z0-9]{5}$",          "1–7 letters + 5 alphanumeric chars"),
        e(IdType.US_DL_WV, "^[A-Z]{1,2}\\d{5,6}$",             "1–2 letters + 5–6 digits"),
        e(IdType.US_DL_WI, "^[A-Z]\\d{13}$",                    "1 letter + 13 digits"),
        e(IdType.US_DL_WY, "^\\d{9,10}$",                       "9–10 digits"),
        e(IdType.US_DL_DC, "^\\d{7}$",                          "Exactly 7 digits")
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
