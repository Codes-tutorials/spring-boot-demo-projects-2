# Spring Boot JWT Authentication Demo

This project demonstrates how to implement JSON Web Token (JWT) authentication in a Spring Boot application using Spring Security. It includes a custom JWT filter, utility class for token generation/validation, and a stateless security configuration.

## 🚀 Tech Stack

*   **Java**: 17 or higher
*   **Spring Boot**: 4.0.1
*   **Spring Security**: 7.x
*   **JWT Library**: JJWT (0.12.6)
*   **Build Tool**: Maven

## 🛠️ Configuration

The application runs on port **8083** to avoid conflicts with default settings.
*   **Port**: `8083` (Configured in `src/main/resources/application.properties`)

## 🏃‍♂️ How to Run

1.  **Clone the repository** (if applicable) or navigate to the project folder.
2.  **Build the project**:
    ```bash
    ./mvnw clean package
    ```
3.  **Run the application**:
    ```bash
    ./mvnw spring-boot:run
    ```
    Or run the JAR file directly:
    ```bash
    java -jar target/spring-boot-jwt-demo-0.0.1-SNAPSHOT.jar
    ```

## 🔐 Authentication Flow

1.  **Login**: User sends `POST` request to `/api/auth/login` with credentials.
2.  **Token Generation**: Server validates credentials and returns a signed JWT.
3.  **Access Protected Resource**: Client sends `GET` request to protected endpoints with `Authorization: Bearer <token>` header.
4.  **Validation**: Server validates the token signature and expiration before granting access.

## 📡 API Endpoints

### 1. Login (Public)
Generates a JWT token for valid credentials.

*   **URL**: `/api/auth/login`
*   **Method**: `POST`
*   **Body**:
    ```json
    {
      "username": "user",
      "password": "password"
    }
    ```
    *(Note: The user is hardcoded in `ApplicationConfig.java` for demonstration purposes.)*

### 2. Protected Resource (Secured)
Requires a valid JWT token.

*   **URL**: `/api/demo/hello`
*   **Method**: `GET`
*   **Headers**:
    *   `Authorization`: `Bearer <your_jwt_token>`

## 🧪 Testing with CURL

**Step 1: Get Token**
```bash
# Windows Command Prompt / PowerShell
curl -X POST http://localhost:8083/api/auth/login ^
 -H "Content-Type: application/json" ^
 -d "{\"username\": \"user\", \"password\": \"password\"}"
```
*Response:*
```json
{"token":"eyJhbGciOiJIUzI1NiJ9..."}
```

**Step 2: Access Protected Endpoint**
Copy the token string from the previous response.
```bash
curl -H "Authorization: Bearer <PASTE_TOKEN_HERE>" http://localhost:8083/api/demo/hello
```
*Response:*
`Hello from secured endpoint`

## 📂 Project Structure

```
src/main/java/com/example/jwt
├── config
│   ├── ApplicationConfig.java    # UserDetails & AuthProvider config
│   └── SecurityConfig.java       # Security Filter Chain setup
├── controller
│   ├── AuthController.java       # Login endpoint
│   └── DemoController.java       # Protected endpoint
├── model
│   ├── AuthRequest.java          # Login DTO
│   └── AuthResponse.java         # Token DTO
└── security
    ├── JwtAuthenticationFilter.java # OncePerRequestFilter for JWT
    └── JwtUtil.java                 # Token generation & validation logic
```
