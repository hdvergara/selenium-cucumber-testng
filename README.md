# Automated Testing Framework

## Overview

This project is an automated testing framework using Selenium with TestNG and Cucumber. It follows the Page Object
Model (POM) design pattern to enhance maintainability and scalability. The framework supports parallel execution and
captures screenshots on test failures.

## Technologies Used

- Java - Programming language.
- Selenium WebDriver - Web automation tool.
- TestNG - Test execution framework.
- Cucumber - BDD framework for writing test scenarios.
- WebDriverManager - Manages browser drivers automatically.
- Maven - Dependency management

## Project Structure

```
java/
  ├── framework/
  │   ├── automation/
  │   │   ├── manager/
  │   │   │   ├── DriverManager.java   # Handles WebDriver initialization
  │   │   ├── pages/
  │   │   │   ├── HomePage.java        # Page Object for Home Page
  │   │   ├── utils/
  │   │   │   ├── ScreenshotUtil.java  # Captures screenshots on test failures
  │   │   │   ├── WebActions.java      # Contains common web actions
  │
  ├── test/
  │   ├── framework/
  │   │   ├── automation/
  │   │   │   ├── hooks/
  │   │   │   │   ├── ConfigurationTestHook.java  # Handles test setup and teardown
  │   │   │   │   ├── TestContext.java  # Manages test execution context
  │   │   │   ├── runners/
  │   │   │   │   ├── TestRunnerStore.java  # Cucumber test runner
  │   │   │   ├── steps/
  │   │   │   │   ├── StoreStepDefinition.java  # Step definitions for feature files
  │
  ├── resources/
  │   ├── features/
  │   │   ├── store.feature  # Cucumber feature file with test scenarios
  │   ├── testng.xml  # TestNG configuration file
```

## Installation & Setup

Ensure you have the following installed:

- Java 11+.
- Maven.
- Chrome browser

## Steps

1. Clone the repository:
    ```
   git clone https://github.com/hdvergara/selenium-cucumber-testng.git
   ```
2. Navigate to the project directory:
    ```
   cd selenium-cucumber-testng
   ```
3. Install dependencies:
    ```
   mvn clean install
   ```

## Running Tests

### Using Maven

To execute all tests:

```
  mvn clean test -Dheadless=true
```

### Using TestRunner file:

Locate and execute from the Play option the file: src/test/java/framework/automation/runners/TestRunnerStore.java

## Reports:
Reports are generated automatically after execution in the following path:

**target/cucumber-html-report/**
