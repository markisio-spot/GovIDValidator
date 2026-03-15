package com.validation.govid;

import com.validation.govid.exception.InvalidIdTypeException;
import com.validation.govid.model.IdType;
import com.validation.govid.model.ValidationResult;
import com.validation.govid.service.GovernmentIdValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GovernmentIdValidatorService}.
 *
 * <p>Tests are grouped by ID type using nested classes for readability.
 * Each group covers at least one valid input, one invalid input, and
 * relevant edge cases (e.g. dashes vs no dashes, correct vs wrong length).
 *
 * <p>The Luhn checksum for {@code CA_SIN} is tested explicitly by using a
 * value that passes the regex pattern but fails the mod-10 check.
 *
 * @author Markisio
 * @version 1.1.0
 */
class GovernmentIdValidatorServiceTest {

    private GovernmentIdValidatorService validator;

    @BeforeEach
    void setUp() {
        validator = new GovernmentIdValidatorService();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CANADA – FEDERAL
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("CA_SIN – Social Insurance Number")
    class CaSin {
        /** Standard format with dashes — the most common user input format. */
        @Test void valid_with_dashes()      { assertValid(IdType.CA_SIN, "046-454-286"); }

        /** No separators — valid as the regex accepts optional dashes. */
        @Test void valid_no_dashes()        { assertValid(IdType.CA_SIN, "046454286"); }

        /** Space separators — valid as the regex also accepts spaces. */
        @Test void valid_with_spaces()      { assertValid(IdType.CA_SIN, "046 454 286"); }

        /** First digit 0 is never assigned by the CRA — must fail regex. */
        @Test void invalid_starts_with_0()  { assertInvalid(IdType.CA_SIN, "012345678"); }

        /**
         * This value passes the regex (correct length, first digit 1–9) but changes
         * the last digit to 7, causing the Luhn checksum to fail.
         */
        @Test void invalid_luhn_failure()   { assertInvalid(IdType.CA_SIN, "046454287"); }

        @Test void invalid_too_short()      { assertInvalid(IdType.CA_SIN, "12345"); }
        @Test void invalid_letters()        { assertInvalid(IdType.CA_SIN, "12A456789"); }
    }

    @Nested
    @DisplayName("CA_PASSPORT – Canadian Passport")
    class CaPassport {
        @Test void valid()                  { assertValid(IdType.CA_PASSPORT, "AB123456"); }

        /** Input normalisation: lowercase letters should be uppercased and accepted. */
        @Test void valid_lowercase()        { assertValid(IdType.CA_PASSPORT, "ab123456"); }

        @Test void invalid_one_letter()     { assertInvalid(IdType.CA_PASSPORT, "A1234567"); }
        @Test void invalid_too_long()       { assertInvalid(IdType.CA_PASSPORT, "AB1234567"); }
    }

    @Nested
    @DisplayName("CA_PR_CARD – Permanent Resident Card")
    class CaPrCard {
        @Test void valid()                  { assertValid(IdType.CA_PR_CARD, "ABCD1234567"); }
        @Test void invalid_short_letters()  { assertInvalid(IdType.CA_PR_CARD, "ABC1234567"); }
        @Test void invalid_short_digits()   { assertInvalid(IdType.CA_PR_CARD, "ABCD123456"); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CANADA – DRIVER LICENCES
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("CA_DL_ON – Ontario Driver Licence")
    class CaDlOntario {
        /** Ontario format encodes DOB in the last 4 digits — [0156] and [0123] are valid month/day indicators. */
        @Test void valid()                  { assertValid(IdType.CA_DL_ON, "A1234-56789 0101"); }
        @Test void valid_no_dash()          { assertValid(IdType.CA_DL_ON, "A123456789 0101"); }
        @Test void invalid_no_letter()      { assertInvalid(IdType.CA_DL_ON, "123456789 0101"); }
    }

    @Nested
    @DisplayName("CA_DL_QC – Quebec Driver Licence")
    class CaDlQuebec {
        @Test void valid()                  { assertValid(IdType.CA_DL_QC, "A123456789012"); }
        @Test void invalid_too_short()      { assertInvalid(IdType.CA_DL_QC, "A12345678901"); }
        @Test void invalid_no_letter()      { assertInvalid(IdType.CA_DL_QC, "1234567890123"); }
    }

    @Nested
    @DisplayName("CA_DL_BC – BC Driver Licence")
    class CaDlBc {
        @Test void valid()                  { assertValid(IdType.CA_DL_BC, "1234567"); }
        @Test void invalid_too_long()       { assertInvalid(IdType.CA_DL_BC, "12345678"); }
        @Test void invalid_too_short()      { assertInvalid(IdType.CA_DL_BC, "123456"); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CANADA – HEALTH CARDS
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("CA_HEALTH_ON – OHIP")
    class CaHealthOn {
        @Test void valid()                   { assertValid(IdType.CA_HEALTH_ON, "ABCD123456XY"); }
        @Test void invalid_missing_suffix()  { assertInvalid(IdType.CA_HEALTH_ON, "ABCD123456"); }
        @Test void invalid_digit_in_prefix() { assertInvalid(IdType.CA_HEALTH_ON, "AB1D123456XY"); }
    }

    @Nested
    @DisplayName("CA_HEALTH_QC – RAMQ")
    class CaHealthQc {
        @Test void valid()                   { assertValid(IdType.CA_HEALTH_QC, "SMIT12345678AB"); }
        @Test void invalid_short_digits()    { assertInvalid(IdType.CA_HEALTH_QC, "SMIT1234567AB"); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // USA – FEDERAL
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("US_SSN – Social Security Number")
    class UsSsn {
        @Test void valid_with_dashes()   { assertValid(IdType.US_SSN, "123-45-6789"); }
        @Test void valid_no_dashes()     { assertValid(IdType.US_SSN, "123456789"); }

        /** Area code 000 is never assigned — SSA specification. */
        @Test void invalid_area_000()    { assertInvalid(IdType.US_SSN, "000-45-6789"); }

        /** Area code 666 is never assigned — SSA specification. */
        @Test void invalid_area_666()    { assertInvalid(IdType.US_SSN, "666-45-6789"); }

        /** Area codes 900–999 are reserved (used for ITINs and other purposes). */
        @Test void invalid_area_900s()   { assertInvalid(IdType.US_SSN, "900-45-6789"); }

        /** Group 00 is never assigned within any area code. */
        @Test void invalid_group_00()    { assertInvalid(IdType.US_SSN, "123-00-6789"); }

        /** Serial 0000 is never assigned within any group. */
        @Test void invalid_serial_0000() { assertInvalid(IdType.US_SSN, "123-45-0000"); }
    }

    @Nested
    @DisplayName("US_PASSPORT – US Passport")
    class UsPassport {
        @Test void valid()               { assertValid(IdType.US_PASSPORT, "123456789"); }
        @Test void invalid_with_letter() { assertInvalid(IdType.US_PASSPORT, "A23456789"); }
        @Test void invalid_too_short()   { assertInvalid(IdType.US_PASSPORT, "12345678"); }
    }

    @Nested
    @DisplayName("US_PASSPORT_CARD")
    class UsPassportCard {
        @Test void valid()               { assertValid(IdType.US_PASSPORT_CARD, "C12345678"); }
        @Test void invalid_wrong_prefix(){ assertInvalid(IdType.US_PASSPORT_CARD, "A12345678"); }
        @Test void invalid_too_short()   { assertInvalid(IdType.US_PASSPORT_CARD, "C1234567"); }
    }

    @Nested
    @DisplayName("US_ITIN – Individual Taxpayer ID")
    class UsItin {
        /** Area 912, group 70 — within the valid IRS-assigned ITIN range. */
        @Test void valid()               { assertValid(IdType.US_ITIN, "912-70-1234"); }

        /** ITINs must start with area code 9xx — non-9 areas are rejected. */
        @Test void invalid_not_9xx()     { assertInvalid(IdType.US_ITIN, "123-70-1234"); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // USA – DRIVER LICENCES (representative sample)
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("US State Driver Licences – representative coverage")
    class UsDl {
        @Test void ca_valid()   { assertValid(IdType.US_DL_CA, "A1234567"); }
        @Test void fl_valid()   { assertValid(IdType.US_DL_FL, "A123456789012"); }
        @Test void ny_valid()   { assertValid(IdType.US_DL_NY, "A1234567"); }
        @Test void tx_valid()   { assertValid(IdType.US_DL_TX, "12345678"); }
        @Test void il_valid()   { assertValid(IdType.US_DL_IL, "A12345678901"); }
        @Test void pa_valid()   { assertValid(IdType.US_DL_PA, "12345678"); }

        /** Pennsylvania requires exactly 8 digits — 7 should fail. */
        @Test void pa_invalid() { assertInvalid(IdType.US_DL_PA, "1234567"); }

        @Test void wa_valid()   { assertValid(IdType.US_DL_WA, "SMITHJ123AB"); }
    }


    // ─────────────────────────────────────────────────────────────────────────
    // CANADA – NEW ID TYPES (v1.1.0)
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("CA_MILITARY_ID – Canadian Armed Forces Service Number")
    class CaMilitaryId {
        @Test void valid()               { assertValid(IdType.CA_MILITARY_ID, "A12345678"); }
        @Test void valid_lowercase()     { assertValid(IdType.CA_MILITARY_ID, "a12345678"); }
        @Test void invalid_no_letter()   { assertInvalid(IdType.CA_MILITARY_ID, "123456789"); }
        @Test void invalid_too_short()   { assertInvalid(IdType.CA_MILITARY_ID, "A1234567"); }
        @Test void invalid_too_long()    { assertInvalid(IdType.CA_MILITARY_ID, "A123456789"); }
    }

    @Nested
    @DisplayName("CA_NEXUS – NEXUS / PASS ID Card")
    class CaNexus {
        /** Valid NEXUS number starting with prefix 10. */
        @Test void valid_prefix_10()     { assertValid(IdType.CA_NEXUS, "102345678"); }
        /** Valid NEXUS number starting with prefix 95. */
        @Test void valid_prefix_95()     { assertValid(IdType.CA_NEXUS, "951234567"); }
        /** Prefix 11 is not a known CBP-assigned NEXUS prefix. */
        @Test void invalid_bad_prefix()  { assertInvalid(IdType.CA_NEXUS, "112345678"); }
        @Test void invalid_too_short()   { assertInvalid(IdType.CA_NEXUS, "10234567"); }
        @Test void invalid_too_long()    { assertInvalid(IdType.CA_NEXUS, "1023456789"); }
    }

    @Nested
    @DisplayName("CA_PAL – Possession and Acquisition Licence")
    class CaPal {
        /** Core 8-digit licence number without sequence suffix. */
        @Test void valid_no_suffix()     { assertValid(IdType.CA_PAL, "12345678"); }
        /** 7-digit core number — also valid. */
        @Test void valid_7_digits()      { assertValid(IdType.CA_PAL, "1234567"); }
        /** Full format with .NNNN issuance sequence suffix. */
        @Test void valid_with_suffix()   { assertValid(IdType.CA_PAL, "12345678.0001"); }
        @Test void invalid_too_short()   { assertInvalid(IdType.CA_PAL, "123456"); }
        @Test void invalid_bad_suffix()  { assertInvalid(IdType.CA_PAL, "12345678.001"); }
        @Test void invalid_letters()     { assertInvalid(IdType.CA_PAL, "1234A678"); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // USA – NEW ID TYPES (v1.1.0)
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("US_MILITARY_ID – DoD Common Access Card (EDIPI)")
    class UsMilitaryId {
        @Test void valid()               { assertValid(IdType.US_MILITARY_ID, "1234567890"); }
        @Test void invalid_too_short()   { assertInvalid(IdType.US_MILITARY_ID, "123456789"); }
        @Test void invalid_too_long()    { assertInvalid(IdType.US_MILITARY_ID, "12345678901"); }
        @Test void invalid_letters()     { assertInvalid(IdType.US_MILITARY_ID, "123456789A"); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STRING API + EDGE CASES
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("String API and edge cases")
    class EdgeCases {

        /** The string-based overload should work identically to the enum overload. */
        @Test void string_api_valid()       { assertValid("CA_SIN", "046-454-286"); }

        /** The string API performs a case-insensitive lookup — "ca_sin" should work. */
        @Test void string_api_lowercase()   { assertValid("ca_sin", "046-454-286"); }

        /** Null value must return an invalid result, not throw an exception. */
        @Test void null_value_fails()       { assertInvalid(IdType.CA_SIN, null); }

        /** Blank value must return an invalid result, not throw an exception. */
        @Test void blank_value_fails()      { assertInvalid(IdType.CA_SIN, "   "); }

        /** An unrecognised type name must throw InvalidIdTypeException. */
        @Test void unknown_type_throws() {
            assertThrows(InvalidIdTypeException.class, () -> validator.validate("CA_BOGUS", "123"));
        }

        /** describeFormat() should return a non-null, non-empty string for any valid IdType. */
        @Test void describe_format_works() {
            assertNotNull(validator.describeFormat(IdType.CA_SIN));
        }

        /** supports() should return true for a well-known ID type. */
        @Test void supports_returns_true()  { assertTrue(validator.supports(IdType.US_SSN)); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Assertion helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void assertValid(IdType type, String value) {
        ValidationResult r = validator.validate(type, value);
        assertTrue(r.isValid(),
                "Expected VALID for [" + type + "] \"" + value + "\": " + r.getFailureReason());
    }

    private void assertInvalid(IdType type, String value) {
        ValidationResult r = validator.validate(type, value);
        assertFalse(r.isValid(),
                "Expected INVALID for [" + type + "] \"" + value + "\"");
    }

    private void assertValid(String typeName, String value) {
        ValidationResult r = validator.validate(typeName, value);
        assertTrue(r.isValid(),
                "Expected VALID for [" + typeName + "] \"" + value + "\": " + r.getFailureReason());
    }
}
