# gov-id-validator

A lightweight Spring Boot library for **format-only** validation of Canadian and US government IDs. No external API calls, no network dependency — pure regex + checksum logic.

---

## Coverage

### Canada
| ID Type | Constant |
|---|---|
| Social Insurance Number (SIN) | `CA_SIN` |
| Passport | `CA_PASSPORT` |
| Permanent Resident Card | `CA_PR_CARD` |
| Citizenship Certificate | `CA_CITIZENSHIP_CERT` |
| Driver Licence – Alberta | `CA_DL_AB` |
| Driver Licence – BC | `CA_DL_BC` |
| Driver Licence – Manitoba | `CA_DL_MB` |
| Driver Licence – New Brunswick | `CA_DL_NB` |
| Driver Licence – NL | `CA_DL_NL` |
| Driver Licence – Nova Scotia | `CA_DL_NS` |
| Driver Licence – Ontario | `CA_DL_ON` |
| Driver Licence – PEI | `CA_DL_PE` |
| Driver Licence – Quebec | `CA_DL_QC` |
| Driver Licence – Saskatchewan | `CA_DL_SK` |
| Driver Licence – NWT / Nunavut / Yukon | `CA_DL_NT / NU / YT` |
| Health Card – ON (OHIP) | `CA_HEALTH_ON` |
| Health Card – BC | `CA_HEALTH_BC` |
| Health Card – AB | `CA_HEALTH_AB` |
| Health Card – QC (RAMQ) | `CA_HEALTH_QC` |
| Health Card – SK / MB | `CA_HEALTH_SK / MB` |

### USA
| ID Type | Constant |
|---|---|
| Social Security Number (SSN) | `US_SSN` |
| Passport | `US_PASSPORT` |
| Passport Card | `US_PASSPORT_CARD` |
| ITIN | `US_ITIN` |
| Employment Authorization (EAD) | `US_EAD` |
| Green Card | `US_GREEN_CARD` |
| Driver Licence – all 50 states + DC | `US_DL_AL` … `US_DL_DC` |

---

## Quick Start

### 1. Add the dependency (after local install)

```xml
<dependency>
    <groupId>com.validation</groupId>
    <artifactId>gov-id-validator</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Inject the service

```java
@Autowired
private GovernmentIdValidatorService idValidator;
```

### 3. Validate

```java
// By enum
ValidationResult result = idValidator.validate(IdType.CA_SIN, "046-454-286");
result.isValid();              // true
result.getFormatDescription(); // "9 digits (###-###-###), first digit 1–9"

// By string name (useful in REST controllers)
ValidationResult r = idValidator.validate("US_DL_FL", "A123456789012");

// Inspect failure
if (!r.isValid()) {
    System.out.println(r.getFailureReason());
}

// Describe a format without validating
String fmt = idValidator.describeFormat(IdType.US_SSN);
// → "9 digits (###-##-####), area ≠ 000/666/900–999"
```

---

## What "format validation" means

This library checks:
- **Structure** — correct length, character types, order of letters/digits
- **Checksum** — Luhn mod-10 on `CA_SIN`; invalid SSN area/group/serial blocks on `US_SSN`

It does **not** check:
- Whether the ID has been issued to a real person
- Whether the ID is expired
- Whether the ID belongs to the person presenting it

For existence/identity checks, layer a KYC provider on top of this.

---

## Build

```bash
mvn clean install
```

---

## Notes

- Input is automatically trimmed and uppercased before matching.
- Health card numbers are accepted for format validation but note that provincial law restricts collection of health card numbers for non-health-care purposes (Ontario PHIPA, etc.). Consult legal before storing them.
