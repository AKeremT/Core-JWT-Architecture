# Core JWT Architecture

A robust, production-ready, and **purely stateless (Zero-DB Hit)** JWT authentication and role-based authorization architecture built with **Spring Boot** and modern **Spring Security**.

## 🚀 Key Features

- **Zero-DB Hit (True Stateless Architecture):** Incoming requests are validated in a single parse operation within the security filter. User identity and authorities are constructed directly from JWT claims without executing database queries (`SELECT`) on every HTTP request.
- **Modern JJWT (0.12.x):** Fully utilizes the latest `io.jsonwebtoken` API (`verifyWith`, `parseSignedClaims`, `getPayload`), avoiding deprecated legacy patterns.
- **Composition over Inheritance:** The domain `User` JPA entity is decoupled from framework-specific security contracts via a dedicated `CustomUserDetails` decorator.
- **REST-Compliant Error Handling:** Custom JSON responses for `401 Unauthorized` (`AuthenticationEntryPoint`) and `403 Forbidden` (`AccessDeniedHandler`), replacing default Spring HTML redirect behaviors.
- **Modern Java Standards:** Powered by Java 21, utilizing immutable Java `record` types for DTOs.

---

> ⚠️ **Disclaimer / Educational Notice:**  
> This project is designed as an educational reference architecture. For ease of local setup and rapid testing, application secrets (e.g., `jwt.secret-key`) and database configurations are **intentionally included** in `application.properties`. This is a deliberate design choice for demonstration purposes and not an inadvertent vulnerability.

---

## 🛠️ Endpoints

| HTTP Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a new user & receive JWT |
| `POST` | `/api/auth/login` | Public | Authenticate credentials & receive JWT |
| `GET` | `/api/private` | Authenticated | Accessible by any user with a valid token |
| `GET` | `/api/private/user` | `USER` or `ADMIN` | Role-protected endpoint |
| `GET` | `/api/private/admin` | `ADMIN` | Restricted to admin users only |
