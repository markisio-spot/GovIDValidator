<p align="center">
  <img src="https://raw.githubusercontent.com/markisio-spot/GovIDValidator/main/gov_id_validator_logo_v2.png" alt="gov-id-validator logo" width="180"/>
</p>

<h1 align="center">gov-id-validator</h1>

<p align="center">
  A lightweight Spring Boot library for <strong>format-only</strong> validation of Canadian and US government IDs.<br/>
  No external API calls, no network dependency — pure regex + checksum logic.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-1.1.0-00ff8c?style=flat-square"/>
  <img src="https://img.shields.io/badge/Java-17-00ff8c?style=flat-square"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-00ff8c?style=flat-square"/>
  <img src="https://img.shields.io/badge/IDs%20supported-99-00ff8c?style=flat-square"/>
  <img src="https://img.shields.io/badge/author-Markisio-00ff8c?style=flat-square"/>
</p>

---

## Coverage

### Canada (41 ID types)
| ID Type | Constant |
|---|---|
| Social Insurance Number (SIN) | `CA_SIN` |
| Passport | `CA_PASSPORT` |
| Permanent Resident Card | `CA_PR_CARD` |
| Citizenship Certificate | `CA_CITIZENSHIP_CERT` |
| Military ID (CAF Service Number) | `CA_MILITARY_ID` |
| NEXUS Card (PASS ID) | `CA_NEXUS` |
| Possession & Acquisition Licence (PAL) | `CA_PAL` |
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

### USA (58 ID types)
| ID Type | Constant |
|---|---|
| Social Security Number (SSN) | `US_SSN` |
| Passport | `US_PASSPORT` |
| Passport Card | `US_PASSPORT_CARD` |
| ITIN | `US_ITIN` |
| Employment Authorization (EAD) | `US_EAD` |
| Green Card | `US_GREEN_CARD` |
| Military ID (DoD CAC / EDIPI) | `US_MILITARY_ID` |
| Driver Licence – all 50 states + DC | `US_DL_AL` … `US_DL_DC` |

---

## Quick Start

### 1. Add the dependency (after local install)

```xml
<dependency>
    <groupId>com.validation</groupId>
    <artifactId>gov-id-validator</artifactId>
    <version>1.1.0</version>
</dependency>
```

### 2. Inject the service

```java
@Autowired
private GovernmentIdValidatorService idValidator;
```

### 3. Validate

```java
// By enum constant
ValidationResult result = idValidator.validate(IdType.CA_SIN, "046-454-286");
result.isValid();              // true
result.getFormatDescription(); // "9 digits (###-###-###), first digit 1–9"

// By string name — useful when idType comes from a REST param or form field
ValidationResult r = idValidator.validate("US_DL_FL", "A123456789012");

// Inspect failure reason
if (!r.isValid()) {
    System.out.println(r.getFailureReason());
}

// Get expected format without validating (e.g. for UI hints)
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
- Whether the ID is expired or revoked
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
- `CA_PAL` (Possession & Acquisition Licence) uses a best-effort pattern — the RCMP does not publish the authoritative format specification.
- Health card numbers are accepted for format validation but provincial law restricts their collection for non-health-care purposes (Ontario PHIPA, etc.). Consult legal before storing them.

---

## Author

**Markisio** — [github.com/markisio-spot](https://github.com/markisio-spot)
