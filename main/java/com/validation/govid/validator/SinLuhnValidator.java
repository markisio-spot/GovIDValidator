package com.validation.govid.validator;

/**
 * Validates the Luhn (mod-10) checksum of a Canadian Social Insurance Number (SIN).
 *
 * <p>The Luhn algorithm is a standard checksum formula used by the Government of Canada
 * to validate SINs. It detects the most common single-digit data-entry errors, including
 * transposed adjacent digits and single-digit substitutions, covering approximately 90%
 * of such mistakes.
 *
 * <p>This validator is intentionally package-private. It is invoked automatically
 * by {@link com.validation.govid.service.GovernmentIdValidatorService} as a second-pass
 * check after the regex pattern for {@code CA_SIN} has already passed. Callers should
 * never invoke this class directly.
 *
 * <p><strong>Algorithm summary:</strong>
 * <ol>
 *   <li>Double the value of every second digit (positions 1, 3, 5, 7 — zero-indexed).</li>
 *   <li>If the doubled value exceeds 9, subtract 9.</li>
 *   <li>Sum all digits (original and doubled).</li>
 *   <li>The SIN is valid if the total sum is divisible by 10 (i.e. {@code sum % 10 == 0}).</li>
 * </ol>
 *
 * @author Markisio
 * @version 1.0.0
 */
final class SinLuhnValidator {

    /** Utility class — not instantiable. */
    private SinLuhnValidator() {}

    /**
     * Returns {@code true} if the given 9-digit SIN string passes the Luhn mod-10 checksum.
     *
     * <p>The input must be exactly 9 numeric characters with no dashes or spaces.
     * The caller ({@code GovernmentIdValidatorService}) is responsible for stripping
     * separators before invoking this method.
     *
     * <p>Example:
     * <pre>{@code
     * // "046-454-286" stripped to "046454286" → valid (sum = 30, 30 % 10 == 0)
     * SinLuhnValidator.isValid("046454286"); // returns true
     *
     * // Single digit changed → invalid
     * SinLuhnValidator.isValid("046454287"); // returns false
     * }</pre>
     *
     * @param digitsOnly exactly 9 digit characters; must not contain dashes, spaces, or letters
     * @return {@code true} if the Luhn checksum passes; {@code false} otherwise
     */
    static boolean isValid(String digitsOnly) {
        if (digitsOnly == null || digitsOnly.length() != 9) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(digitsOnly.charAt(i));

            // Double every second digit (zero-indexed positions 1, 3, 5, 7)
            if (i % 2 == 1) {
                digit *= 2;
                // If doubling produces a two-digit number, reduce by subtracting 9
                // (equivalent to summing the individual digits: e.g. 14 → 1+4 = 5 = 14-9)
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }

        // Valid SINs produce a total sum that is a multiple of 10
        return sum % 10 == 0;
    }
}
