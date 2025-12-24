# Spring Boot AOP Demo

This project demonstrates the usage of **Spring AOP (Aspect Oriented Programming)** to handle cross-cutting concerns such as logging, exception handling, and transaction management in a clean and modular way.

## 🚀 Tech Stack

*   **Java**: 17 or higher
*   **Spring Boot**: 4.0.1
*   **Spring AOP**: AspectJ Weaver
*   **Database**: H2 In-Memory Database

## 🛠️ Key Features

### 1. Logging Aspect
*   **File**: `LoggingAspect.java`
*   **Functionality**: Intercepts method executions in Controllers and Services using `@Around` advice.
*   **Behavior**: Logs the execution time of methods, helping to identify performance bottlenecks.

### 2. Exception Handling Aspect
*   **File**: `ExceptionLoggingAspect.java`
*   **Functionality**: Intercepts exceptions thrown from Service layer using `@AfterThrowing` advice.
*   **Behavior**: Centralized logging of runtime exceptions without cluttering business logic.

### 3. Transaction Management
*   **File**: `AccountService.java`
*   **Functionality**: Demonstrates declarative transaction management with `@Transactional`.
*   **Behavior**: Ensures data consistency by rolling back transactions when runtime exceptions occur (e.g., during fund transfers).

## 🏃‍♂️ How to Run

1.  **Navigate to the project directory**:
    ```bash
    cd spring-aop-demo
    ```
2.  **Run the application**:
    ```bash
    ../mvnw spring-boot:run
    ```
    *Note: Use `..\mvnw` on Windows Command Prompt.*

## 🧪 Testing

You can verify the AOP behavior by checking the application logs after interacting with the system or running the provided tests:

```bash
../mvnw test
```

### Expected Logs Example
```
INFO  LoggingAspect : void com.example.aop.AccountService.transfer(..) took 15 ms
ERROR ExceptionLoggingAspect : void com.example.aop.AccountService.transfer(..) threw java.lang.IllegalArgumentException: Insufficient funds
```
