# Full-Stack QA Automation Framework

![CI](https://github.com/okurenes/full-stack-qa-automation/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-blue)
![Selenium](https://img.shields.io/badge/Selenium-4.x-green)
![REST Assured](https://img.shields.io/badge/REST--Assured-5.x-orange)

Enterprise-grade test automation framework built with Java, covering UI, API, BDD, and Performance testing layers.

## Framework Architecture

```
┌─────────────────────────────────────────────┐
│             Test Suites (TestNG)            │
├──────────┬──────────┬──────────┬────────────┤
│  UI Tests│ API Tests│ BDD Tests│  Perf Tests│
│ Selenium │  REST    │ Cucumber │  Java Load │
│   + POM  │ Assured  │ Gherkin  │  Runner    │
├──────────┴──────────┴──────────┴────────────┤
│          Base Classes + Config              │
├─────────────────────────────────────────────┤
│     Allure Reports + Log4j2 + CI/CD        │
└─────────────────────────────────────────────┘
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI Testing | Selenium WebDriver 4 + Page Object Model |
| API Testing | REST Assured 5 + JSON Schema Validation |
| BDD | Cucumber 7 + Gherkin |
| Performance | Custom Java Load Runner (p95/p99 metrics) |
| Test Runner | TestNG 7 |
| Reporting | Allure Reports 2 |
| CI/CD | GitHub Actions |
| Logging | Log4j2 |

## Project Structure

```
src/test/
├── java/com/qa/automation/
│   ├── base/           # BaseTest (WebDriver), BaseApiTest (REST Assured config)
│   ├── ui/
│   │   ├── pages/      # Page Object Model (Login, Inventory, Cart, Checkout)
│   │   └── tests/      # TestNG UI test classes
│   ├── api/
│   │   ├── endpoints/  # API endpoint constants
│   │   ├── models/     # POJO models (User)
│   │   └── tests/      # REST Assured test classes
│   ├── bdd/
│   │   ├── steps/      # Cucumber step definitions
│   │   └── runner/     # CucumberRunner
│   └── performance/    # Load & stress test runner
└── resources/
    ├── features/        # Gherkin .feature files
    ├── schemas/         # JSON Schema files for API validation
    ├── config.properties
    └── log4j2.xml
```

## Test Coverage

### UI Tests — SauceDemo (saucedemo.com)
- Login: valid/invalid credentials, locked user, multiple user types (data-driven)
- Inventory: product count, add to cart, multi-add, cart badge sync
- Checkout: full E2E purchase, missing field validations, order summary

### API Tests — ReqRes (reqres.in)
- **CRUD**: GET list, GET by ID, POST create, PUT update, PATCH partial, DELETE
- **Auth**: Registration success/failure, Login success/failure
- **Schema validation**: JSON Schema for user response structure
- **Response time SLA**: All responses must be < 3000ms

### BDD — Cucumber / Gherkin
- Login feature with Scenario Outline (parameterized user types)
- User API feature covering CRUD operations

### Performance Tests
- **Smoke load**: 10 VUs, 20 requests — p95 < 3s
- **Standard load**: 25 VUs, 50 requests over 60s — real-world traffic
- **Stress test**: 50 VUs, 100 requests — p99 < 5s, error rate < 5%
- Metrics: throughput, min/avg/max, p95, p99 latency

## Prerequisites

- Java 17+
- Maven 3.8+
- Chrome browser (for UI tests; auto-managed via WebDriverManager)

## Running Tests

```bash
# All tests
mvn test

# API tests only
mvn test -Dgroups="api"

# UI tests only (headed)
mvn test -Dgroups="ui" -Dheadless=false

# UI tests headless
mvn test -Dgroups="ui" -Dheadless=true

# BDD / Cucumber tests
mvn test -Dtest=CucumberRunner

# Performance tests
mvn test -Dgroups="performance"

# Smoke suite only
mvn test -Dgroups="smoke"
```

## Allure Report

```bash
# Generate and open report
mvn allure:serve

# Generate report only
mvn allure:report
# Open target/site/allure-maven-plugin/index.html
```

## CI/CD Pipeline

GitHub Actions runs on every push and pull request:

1. **API Tests** — runs first, fast feedback
2. **UI Tests** — headless Chrome, parallel classes
3. **Performance Tests** — runs after API tests pass
4. **Allure Report** — aggregates all results, deploys to GitHub Pages on `main`

Scheduled nightly run: weekdays at 06:00 UTC.

## Troubleshooting

**Chrome not found on CI**
Ensure the `browser-actions/setup-chrome` step runs before the test step. Locally, WebDriverManager downloads the driver automatically.

**Allure report is empty**
Run `mvn test` first to generate `target/allure-results`, then `mvn allure:serve`.

**Tests timing out**
Increase `explicit.wait` in `config.properties` or set `-Dexplicit.wait=30` on the command line.

**REST Assured SSL errors**
Add `.relaxedHTTPSValidation()` to `RequestSpecBuilder` in `BaseApiTest` for self-signed certs.
