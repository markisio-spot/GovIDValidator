package com.validation.govid.model;

/**
 * Two-letter country codes for the jurisdictions supported by this library.
 *
 * <p>Used as a grouping and filtering aid — for example, to retrieve all
 * ID types for a specific country from the validator service.
 *
 * @author Markisio
 * @version 1.0.0
 */
public enum Country {

    /** Canada — supports federal IDs, provincial driver licences, and health cards. */
    CA,

    /** United States — supports federal IDs and driver licences for all 50 states + D.C. */
    US
}
