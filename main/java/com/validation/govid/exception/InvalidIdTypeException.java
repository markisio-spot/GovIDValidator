package com.validation.govid.exception;

/**
 * Thrown by {@link com.validation.govid.service.GovernmentIdValidatorService}
 * when an unrecognised or null {@link com.validation.govid.model.IdType} is supplied.
 *
 * <p>This is a runtime exception. Callers using the string-based overload
 * {@code validate(String idTypeName, String value)} should handle this exception
 * when the {@code idTypeName} is sourced from user input or an external system,
 * as it may not correspond to a known {@link com.validation.govid.model.IdType} constant.
 *
 * <p>Note: a <em>failed</em> validation (wrong format) does NOT throw this exception —
 * it returns a {@link com.validation.govid.model.ValidationResult} with {@code valid=false}.
 * This exception is reserved for unrecognised ID type identifiers only.
 *
 * @author Markisio
 * @version 1.0.0
 */
public class InvalidIdTypeException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidIdTypeException} with the given detail message.
     *
     * @param message description of the unrecognised ID type, e.g. {@code "Unknown IdType: XX_BOGUS"}
     */
    public InvalidIdTypeException(String message) {
        super(message);
    }
}
