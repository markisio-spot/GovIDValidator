package com.validation.govid.model;

/**
 * Enumeration of all supported government ID types for Canada and the United States.
 *
 * <p>Naming convention: {@code {COUNTRY}_{CATEGORY}_{JURISDICTION}}
 * <ul>
 *   <li>{@code CA_*}        — Canadian IDs</li>
 *   <li>{@code US_*}        — United States IDs</li>
 *   <li>{@code *_DL_*}      — Driver Licence, followed by province/state code</li>
 *   <li>{@code *_HEALTH_*}  — Provincial health card</li>
 * </ul>
 *
 * <p>Pass these constants to
 * {@link com.validation.govid.service.GovernmentIdValidatorService#validate(IdType, String)}
 * to validate a specific ID format.
 *
 * @author Markisio
 * @version 1.1.0
 */
public enum IdType {

    // ─── CANADA – FEDERAL ─────────────────────────────────────────────────────

    /**
     * Social Insurance Number — 9-digit numeric, first digit 1–9.
     * Also validated via Luhn mod-10 checksum to catch transposition errors.
     */
    CA_SIN,

    /** Canadian Passport — 2 uppercase letters + 6 digits (e.g. AB123456). */
    CA_PASSPORT,

    /** Permanent Resident Card — 4 uppercase letters + 7 digits (e.g. ABCD1234567). */
    CA_PR_CARD,

    /** Citizenship Certificate — same format as passport: 2 letters + 6 digits. */
    CA_CITIZENSHIP_CERT,

    /**
     * Canadian Armed Forces Service Number — issued to CAF members and DND civilians
     * on the NDI 20 identity card. Format: 1 uppercase letter followed by 8 digits
     * (e.g. A12345678).
     */
    CA_MILITARY_ID,

    /**
     * NEXUS Card (PASS ID) — trusted traveller card issued jointly by the Canada Border
     * Services Agency (CBSA) and US Customs and Border Protection (CBP).
     * Format: exactly 9 digits, typically beginning with a known CBP-assigned prefix
     * (10, 13, 14, 15, 16, 50, 70, 80, 95, 98, or 99).
     */
    CA_NEXUS,

    /**
     * Possession and Acquisition Licence (PAL) — issued by the RCMP to individuals
     * licensed to possess and acquire firearms in Canada.
     * Format: 7–8 digits, optionally followed by a decimal point and a 4-digit
     * issuance sequence number (e.g. 12345678 or 12345678.0001).
     *
     * <p><strong>Note:</strong> The RCMP does not publicly document the exact digit count
     * of the core licence number. This pattern is best-effort based on observed card formats
     * and may require adjustment as new licence series are issued.
     */
    CA_PAL,

    // ─── CANADA – DRIVER LICENCES ─────────────────────────────────────────────

    /** Alberta — 5–9 digits, or 6 digits-dash-3 digits. */
    CA_DL_AB,
    /** British Columbia — exactly 7 digits. */
    CA_DL_BC,
    /** Manitoba — structured alphanumeric (2L-2L-2L-1L###2L). */
    CA_DL_MB,
    /** New Brunswick — 5–7 digits. */
    CA_DL_NB,
    /** Newfoundland and Labrador — 1 letter + 9 digits. */
    CA_DL_NL,
    /** Nova Scotia — 5 letters + encoded date segment + 6 digits. */
    CA_DL_NS,
    /** Ontario — 1 letter + 4 digits + 5 digits + 4 DOB-encoded digits. */
    CA_DL_ON,
    /** Prince Edward Island — 5–6 digits. */
    CA_DL_PE,
    /** Quebec — 1 letter + 12 digits. */
    CA_DL_QC,
    /** Saskatchewan — exactly 8 digits. */
    CA_DL_SK,
    /** Northwest Territories — exactly 6 digits. */
    CA_DL_NT,
    /** Nunavut — 5–6 digits. */
    CA_DL_NU,
    /** Yukon — 1–6 digits. */
    CA_DL_YT,

    // ─── CANADA – PROVINCIAL HEALTH CARDS ────────────────────────────────────

    /**
     * Ontario Health Insurance Plan (OHIP) — 4 letters + 6 digits + 2 letters.
     * <p><strong>Legal note:</strong> Ontario PHIPA restricts collection of OHIP numbers
     * to health care purposes only.
     */
    CA_HEALTH_ON,
    /** BC Services Card — exactly 10 digits. */
    CA_HEALTH_BC,
    /** Alberta Health Care Number — exactly 9 digits. */
    CA_HEALTH_AB,
    /** Quebec RAMQ number — 4 letters + 8 digits + 2 letters (e.g. SMIT12345678AB). */
    CA_HEALTH_QC,
    /** Saskatchewan Health Card — exactly 9 digits. */
    CA_HEALTH_SK,
    /** Manitoba Personal Health Identification Number (PHIN) — exactly 9 digits. */
    CA_HEALTH_MB,

    // ─── USA – FEDERAL ────────────────────────────────────────────────────────

    /**
     * Social Security Number — 9 digits with optional dashes.
     * Area codes 000, 666, and 900–999 are invalid. Group 00 and serial 0000 are invalid.
     */
    US_SSN,

    /** US Passport book — exactly 9 digits. */
    US_PASSPORT,

    /** US Passport Card — letter C + 8 alphanumeric characters (e.g. C12345678). */
    US_PASSPORT_CARD,

    /**
     * Individual Taxpayer Identification Number (ITIN) — issued by the IRS.
     * Area code always starts with 9; group range is 70–99.
     */
    US_ITIN,

    /** Employment Authorization Document (EAD) — 3 letters + 10 digits. */
    US_EAD,

    /** US Permanent Resident Card (Green Card) — 3 letters + 9 digits. */
    US_GREEN_CARD,

    /**
     * US Military Common Access Card (CAC) — DoD Electronic Data Interchange
     * Personal Identifier (EDIPI). Issued by the Department of Defense to active
     * military personnel, reservists, DoD civilians, and eligible contractors.
     * Format: exactly 10 digits (e.g. 1234567890).
     */
    US_MILITARY_ID,

    // ─── USA – DRIVER LICENCES (all 50 states + DC) ──────────────────────────

    /** Alabama — 1–7 digits. */             US_DL_AL,
    /** Alaska — 1–7 digits. */              US_DL_AK,
    /** Arizona — 1L+8d or 9 digits. */      US_DL_AZ,
    /** Arkansas — 4–9 digits. */            US_DL_AR,
    /** California — 1 letter + 7 digits. */ US_DL_CA,
    /** Colorado — 9d or 1–2L+3–6d. */       US_DL_CO,
    /** Connecticut — 9 digits. */           US_DL_CT,
    /** Delaware — 1–7 digits. */            US_DL_DE,
    /** Florida — 1 letter + 12 digits. */   US_DL_FL,
    /** Georgia — 7–9 digits. */             US_DL_GA,
    /** Hawaii — H+8d or 9 digits. */        US_DL_HI,
    /** Idaho — 2L+6d+1L. */                 US_DL_ID,
    /** Illinois — 1L+11–12d. */             US_DL_IL,
    /** Indiana — 10d or 1L+9d. */           US_DL_IN,
    /** Iowa — 9d or 9d+2L. */               US_DL_IA,
    /** Kansas — 1L+8d or 9d. */             US_DL_KS,
    /** Kentucky — 1L+8–9d. */               US_DL_KY,
    /** Louisiana — 1–9 digits. */           US_DL_LA,
    /** Maine — 7d or 7d+1L. */              US_DL_ME,
    /** Maryland — 1L+12d. */                US_DL_MD,
    /** Massachusetts — S+8d or 9d. */       US_DL_MA,
    /** Michigan — 1L+12d. */                US_DL_MI,
    /** Minnesota — 1L+12d. */               US_DL_MN,
    /** Mississippi — 9 digits. */           US_DL_MS,
    /** Missouri — 1L+5–9d or 9d. */         US_DL_MO,
    /** Montana — 13d or 1L+8d. */           US_DL_MT,
    /** Nebraska — 1L+6–8d. */               US_DL_NE,
    /** Nevada — 9–10d, 12d, or X+8d. */     US_DL_NV,
    /** New Hampshire — 2d+3L+5d. */         US_DL_NH,
    /** New Jersey — 1L+14d. */              US_DL_NJ,
    /** New Mexico — 8–9 digits. */          US_DL_NM,
    /** New York — 1L+7–18d or 8–9d. */      US_DL_NY,
    /** North Carolina — 1–12 digits. */     US_DL_NC,
    /** North Dakota — 3L+6d or 9d. */       US_DL_ND,
    /** Ohio — 2L+6d or 1L+8d. */            US_DL_OH,
    /** Oklahoma — 1L+9d or 9d. */           US_DL_OK,
    /** Oregon — 1–9 digits. */              US_DL_OR,
    /** Pennsylvania — exactly 8 digits. */  US_DL_PA,
    /** Rhode Island — 7d or V+6d. */        US_DL_RI,
    /** South Carolina — 5–11 digits. */     US_DL_SC,
    /** South Dakota — 6–10 digits. */       US_DL_SD,
    /** Tennessee — 7–9 digits. */           US_DL_TN,
    /** Texas — 7–8 digits. */               US_DL_TX,
    /** Utah — 4–10 digits. */               US_DL_UT,
    /** Vermont — 8d or 7d+1L. */            US_DL_VT,
    /** Virginia — 1L+8–11d or 9d. */        US_DL_VA,
    /** Washington — 1–7L+5 alphanumeric. */ US_DL_WA,
    /** West Virginia — 1–2L+5–6d. */        US_DL_WV,
    /** Wisconsin — 1L+13d. */               US_DL_WI,
    /** Wyoming — 9–10 digits. */            US_DL_WY,
    /** Washington D.C. — exactly 7 digits. */ US_DL_DC
}
