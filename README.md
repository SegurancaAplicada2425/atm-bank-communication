# ATM-Bank Communication Project
This workspace contains a project for implementing secure communication between an ATM client and a Bank server. The project is organized into two three main Java modules, built with Gradle.

## Project Structure
- **common**: - Common library with shared classes
- **bank**: Bank server module
- **atm**: ATM client module

## Prerequisites
- Java JDK 11 or higher

## Setup

1. Clone the project
    ```bash
    git clone https://github.com/SegurancaAplicada2425/atm-bank-communication
    cd atm-bank-communication
    ```

2. Compile the project
    ```bash
    ./gradlew shadowJar
    ```
3. Run the Bank server:
    ```bash
    cd bank/build/libs
    java -jar bank-1.0.0-all.jar <parameters>
    ```

4. Run the ATM client:
    ```bash
    cd atm/build/libs
    java -jar atm-1.0.0-all.jar <parameters>
    ```

**Make sure to run the Bank server first before starting the ATM client.**
