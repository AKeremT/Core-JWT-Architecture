# Core JWT Architecture

A robust, production-ready, and **purely stateless (Zero-DB Hit)** JWT authentication and role-based authorization architecture featuring **Refresh Token Rotation (RTR)**, built with **Spring Boot** and modern **Spring Security**.

## 🚀 Key Features

- **Zero-DB Hit (True Stateless Access Tokens):** Incoming requests are validated in a single parse operation within the security filter. User identity and authorities are constructed directly from JWT claims without executing database queries (`SELECT`) on every HTTP request.
- **Dual-Token Architecture:** 
  - **Access Token:** Short-lived (`15m`), stateless, signed JWT for API authorization.
  - **Refresh Token:** Long-lived (`7d`), opaque (`UUID`), stored in the database for issuing new access tokens.
- **Refresh Token Rotation (RTR):** Follows the **OWASP Gold Standard**; every refresh operation revokes/deletes the old refresh token and issues a new pair, preventing replay and token hijacking attacks.
- **JPA `@EntityGraph` Optimization:** Leverages `@EntityGraph(attributePaths = {"user"})` to eagerly fetch the associated `User` with a single SQL `JOIN`, eliminating N+1 queries and avoiding `LazyInitializationException`.
- **Modern JJWT (0.12.x):** Fully utilizes the latest `io.jsonwebtoken` API (`verifyWith`, `parseSignedClaims`, `getPayload`), avoiding deprecated legacy patterns.
- **Composition over Inheritance:** The domain `User` JPA entity is decoupled from framework-specific security contracts via a dedicated `CustomUserDetails` decorator.
- **REST-Compliant Error Handling:** Custom JSON responses for `401 Unauthorized` (`AuthenticationEntryPoint`), `403 Forbidden` (`AccessDeniedHandler`), `409 Conflict`, and `BadCredentialsException`.
- **Modern Java Standards:** Powered by Java 21, utilizing immutable Java `record` types for DTOs.

---

> ⚠️ **Disclaimer / Educational Notice:**  
> This project is designed as an educational reference architecture. For ease of local setup and rapid testing, application secrets (e.g., `jwt.secret-key`) and database configurations are **intentionally included** in `application.properties`. This is a deliberate design choice for demonstration purposes and not an inadvertent vulnerability.

---

## 🛠️ Endpoints

| HTTP Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a new user & receive Access + Refresh tokens |
| `POST` | `/api/auth/login` | Public | Authenticate credentials & receive Access + Refresh tokens |
| `POST` | `/api/auth/refresh` | Public | Exchange a valid Refresh Token for a new token pair (RTR) |
| `GET` | `/api/private` | Authenticated | Accessible by any user with a valid access token |
| `GET` | `/api/private/user` | `USER` or `ADMIN` | Role-protected user endpoint |
| `GET` | `/api/private/admin` | `ADMIN` | Restricted to admin users only |

---

## 📚 Guides & Documentation
- **[`entity-graph-guide.html`](entity-graph-guide.html):** Interactive visual guide explaining JPA `@EntityGraph`, Lazy Loading pitfalls, N+1 problems, and real-life analogies.
