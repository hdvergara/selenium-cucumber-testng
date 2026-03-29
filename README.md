<div align="center">

# UI test automation framework

**Selenium · Cucumber · TestNG · Maven**

[![Java](https://img.shields.io/badge/Java-22%2B-437291?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Selenium](https://img.shields.io/badge/Selenium-4.41-43B02A?style=for-the-badge&logo=selenium&logoColor=white)](https://www.selenium.dev/)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.34-23D96C?style=for-the-badge&logo=cucumber&logoColor=white)](https://cucumber.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.12-FF6C37?style=for-the-badge)](https://testng.org/)
[![Allure](https://img.shields.io/badge/Allure-2.33-FF69B4?style=for-the-badge)](https://docs.qameta.io/allure/)

End-to-end web automation with **BDD**, **Page Object Model** extended with **reusable UI components**, **parallel** execution per thread, and **reporting** (Cucumber HTML + Allure).

[Requirements](#requirements) · [Configuration](#configuration) · [Running tests](#running-tests) · [Project layout](#project-layout) · [Architecture](#architecture) · [Reports](#reports) · [CI/CD](#cicd)

</div>

---

## Overview

This project exercises a demo **OpenCart** store. Scenarios are written in **Gherkin**, steps bind to **TestNG**, and the browser is driven with **Selenium 4** (Selenium Manager resolves the Chrome driver).

Highlights:

| Area | Details |
|------|---------|
| **Concurrency** | One `WebDriver` per thread (`ThreadLocal` in `DriverManager`) for parallel Cucumber + TestNG. |
| **POM + components** | `HomePage` composes `SearchComponent`, `MiniCartComponent`, and `ShoppingCartTableComponent`. |
| **Configuration** | Base URL, timeouts, and `headless` are centralized in `config.properties` via `ConfigLoader`. |
| **Assertions** | Business rules live in **step definitions** (TestNG `Assert`); page objects return data or booleans. |
| **Evidence** | Inline screenshots and Allure-ready output under `target/allure-results`. |

---

## Requirements

| Tool | Notes |
|------|--------|
| **JDK 22+** | `maven-enforcer-plugin` enforces `[22,)`. |
| **Maven 3.x** | Build and test execution. |
| **Google Chrome** | Target browser (driver resolved by Selenium Manager). |

---

## Configuration

The source of truth is **`src/main/resources/config.properties`** (Maven copies it to `target/classes`).

| Property | Example | Description |
|----------|---------|-------------|
| `base.url` | `https://opencart.abstracta.us/` | Application entry URL. |
| `timeout.explicit` | `10` | Explicit wait (seconds) for actions and components. |
| `timeout.implicit` | `2` | Driver implicit wait (seconds). |
| `browser` | `chrome` | Intended browser (current runner uses Chrome). |
| `headless` | `false` | Headless mode; can be overridden with `-Dheadless=true`. |

---

## Running tests

**Clone and run**

```bash
git clone https://github.com/hdvergara/selenium-cucumber-testng.git
cd selenium-cucumber-testng
mvn clean test
```

**Typical Maven run** (Cucumber scenarios via `TestRunnerStore`):

```bash
mvn clean test
```

**Headless** (system property, aligned with `DriverManager` / `ConfigLoader`):

```bash
mvn clean test -Dheadless=true
```

**From the IDE**  
Run `src/test/java/framework/automation/runners/TestRunnerStore.java` as a TestNG test.

---

## Project layout

```
src/main/java/framework/automation/
├── components/          # Reusable UI (search, mini-cart, cart table)
│   ├── SearchComponent.java
│   ├── MiniCartComponent.java
│   └── ShoppingCartTableComponent.java
├── manager/
│   └── DriverManager.java      # Per-thread WebDriver (ThreadLocal)
├── pages/
│   └── HomePage.java           # Component composition + “Add to cart” on search results
└── utils/
    ├── ConfigLoader.java
    ├── WebActions.java
    └── ScreenshotUtil.java

src/test/java/framework/automation/
├── hooks/                 # Scenario context, teardown (e.g. screenshots / Allure)
├── runners/
│   └── TestRunnerStore.java
└── steps/
    └── StoreStepDefinition.java

src/test/java/resources/
├── features/
│   └── store.feature
└── testng.xml
```

---

## Architecture

```mermaid
flowchart LR
  subgraph test ["Test layer"]
    Feature[Gherkin feature]
    Steps[Step definitions]
    Feature --> Steps
  end
  subgraph ui ["UI"]
    Home[HomePage]
    S[SearchComponent]
    M[MiniCartComponent]
    T[ShoppingCartTableComponent]
    Home --> S
    Home --> M
    Home --> T
  end
  Steps --> Home
  Home --> DM[DriverManager / current-thread WebDriver]
```

- **`WebActions`** wraps click, text input, and visibility; each instance receives the **same** `WebDriver` as the rest of the stack (no static `WebDriver` in action helpers).
- **Dynamic locators** (by product name) live in `ShoppingCartTableComponent` where applicable.

---

## Reports

| Type | Location / usage |
|------|-------------------|
| **Cucumber (HTML)** | After `mvn verify`, HTML report under `target/cucumber-html-reports/` (`maven-cucumber-reporting`; expects `cucumber.json` in `target`). |
| **Allure** | Results in `target/allure-results`. View with the Allure CLI, e.g. `allure serve target/allure-results`. |

---

## CI/CD

Continuous integration runs on **GitHub Actions** via **`.github/workflows/maven_tests.yml`** (workflow name: **Maven Tests**).

**Triggers**

| Trigger | When it runs |
|---------|----------------|
| **push** | Commits pushed to **`master`** or **`main`**. |
| **pull_request** | Pull requests targeting **`master`** or **`main`**. |
| **schedule** | Cron **`0 9 */14 * *`** — at **09:00 UTC** on days of the month aligned with that pattern (approximately **biweekly** cadence). |

**Runner and toolchain**

| Item | Value |
|------|--------|
| **OS** | `ubuntu-latest` |
| **JDK** | **22**, **Eclipse Temurin** (`actions/setup-java@v4`) |
| **Maven** | Dependency **cache** enabled (`cache: maven`) for faster builds |
| **Browser** | **Google Chrome** (`google-chrome-stable`) installed on the runner before tests |

**Pipeline steps (summary)**

1. Checkout the repository (`actions/checkout@v4`).
2. Set up **Java 22 (Temurin)** with **Maven cache**.
3. Print `java -version` and `mvn --version` for traceability.
4. Install **Google Chrome** via `apt-get`.
5. Run tests: **`mvn test -Dheadless=true`**.
6. **Upload artifact** **`target/allure-results/`** with **`actions/upload-artifact@v4`**, using **`if: always()`** so Allure raw results are kept **whether the job passes or fails** (`if-no-files-found: warn`, **14-day** retention).
7. **Job `deploy-allure`** (only on **`push`** / **`schedule`** to **`main`** or **`master`**, not on pull requests): downloads the **`allure-results`** artifact, installs the **Allure CLI** (`allure-commandline` via npm), runs **`allure generate`**, then publishes the static site with **`actions/upload-pages-artifact`** and **`actions/deploy-pages`**. This job runs when tests **pass or fail** (not when the workflow is cancelled), so the public URL usually shows the **latest run** including **failed** scenarios, attachments, and steps.

Download workflow artifacts from the workflow run page in GitHub (**Actions** → select run → **Artifacts**).

### GitHub Pages (browse Allure by URL)

After a **one-time** repository configuration, the latest Allure HTML report is published as a static site.

1. In the GitHub repository, go to **Settings** → **Pages**.
2. Under **Build and deployment** → **Source**, select **GitHub Actions** (not “Deploy from a branch”).
3. Save if prompted. The workflow **`.github/workflows/maven_tests.yml`** already includes the **`deploy-allure`** job with `permissions: pages: write` and `id-token: write`.

**Public URL** (project site, default):

`https://<your-github-username>.github.io/<repository-name>/`

Example for this repo: `https://hdvergara.github.io/selenium-cucumber-testng/`

The site updates after a run on **`main`** or **`master`** finishes the **`deploy-allure`** job—**including failed test runs**, so you can open the same URL to inspect **what broke** (subject to Allure having written results under `target/allure-results` before the JVM exited). Pull requests still run tests but **do not** deploy Pages (avoids permission issues and overwriting the site with every PR); use the **Artifacts** tab on the PR run for raw Allure data.

**Local preview** (same as before): `allure serve target/allure-results` after `mvn test`.

**If `deploy-allure` fails**

1. **Settings → Pages → Build and deployment → Source** must be **GitHub Actions** (not “Deploy from a branch”).
2. The workflow uses **`actions/upload-pages-artifact@v4`** (required on GitHub.com; older v3 flows are deprecated for Pages).
3. If the repo or organization uses **environment protection rules** for `github-pages`, approve the deployment in the Actions run when prompted.
4. **Forks** often cannot deploy Pages to the parent repo URL; run from the upstream repo or use artifacts only.

---

## License & author

Sample / portfolio automation project. Adjust license and contact details for your organization.
