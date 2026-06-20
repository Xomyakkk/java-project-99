# Task Manager

Веб приложение для управления задачами, разработанный с использованием **Spring Boot** в рамках учебной программы проекта [Hexlet](https://ru.hexlet.io) по Java. Сервис поддерживает аутентификацию пользователей через JWT, операции CRUD с задачами, фильтрацию по статусу/исполнителю/меткам и интегрируется с SonarQube для непрерывного мониторинга качества кода.

> Демо: https://java-project-99-pu4a.onrender.com

---

### Hexlet tests and linter status:
[![Actions Status](https://github.com/Xomyakkk/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/Xomyakkk/java-project-99/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-99&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-99)

---

## Возможности

- **Аутентификация** — конечная точка входа на основе JWT (`POST /api/login`)
- **Пользователи** — регистрация, обновление, удаление, список
- **Статусы задач** — CRUD для статусов рабочего процесса (например, *К публикации*, *Черновик*, *На рассмотрении*)
- **Метки** — CRUD для пометки задач
- **Задачи** — полный CRUD с фильтрацией по:

- `titleCont` — поиск подстроки в имени задачи

- `assigneeId` — задачи, назначенные конкретному пользователю

- `status` — задачи в определенном статусе (по slug)

- `labelId` — задачи, помеченные определенной меткой
- **Валидация** — проверка полезной нагрузки запроса с помощью Bean Validation (`jakarta.validation`)
- **OpenAPI / Swagger** — интерактивная документация API по адресу `/swagger-ui.html`
- **Централизованная обработка ошибок** — единообразные JSON-ответы об ошибках
- **Поддержка баз данных** — H2 (разработка/тестирование) и PostgreSQL (релиз)

---

## Технический стек

| Layer            | Technology                                        |
|------------------|---------------------------------------------------|
| Language         | Java 21                                           |
| Framework        | Spring Boot 4.0.6                                 |
| Persistence      | Spring Data JPA, Hibernate                        |
| Database         | H2 (in-memory), PostgreSQL                        |
| Security         | Spring Security, JJWT 0.12.3                      |
| API docs         | springdoc-openapi 2.8.4                           |
| Build tool       | Gradle 9.5 (Kotlin DSL)                           |
| Code quality     | SonarQube, Checkstyle, SpotBugs                   |
| Error tracking   | Sentry                                            |
| Deployment       | Render                                            |

---

## Архитектура

```
src/main/java/hexlet/code/app/
├── AppApplication.java          # Spring Boot entry point
├── config/                      # Security, Swagger, DataInitializer, Password
├── controller/                  # REST controllers (Auth, User, Task, Label, TaskStatus, Welcome)
├── dto/                         # Request/response DTOs
├── exception/                   # GlobalExceptionHandler
├── model/                       # JPA entities (User, Task, Label, TaskStatus)
├── repository/                  # Spring Data repositories + TaskSpecification
├── security/                    # JwtTokenProvider, JwtAuthenticationFilter, UserDetailsService
└── service/                     # Business logic
```

Приложение построено на классической многоуровневой архитектуре: **контроллер → сервис → репозиторий**, с DTO на границе, позволяющими отделить формат передачи данных от модели хранения.

---

## Getting Started

### Prerequisites

- Java 21
- Gradle 9.5+ (or use the included `./gradlew` wrapper)

### Run locally (H2)

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`. H2 console is available at `/h2-console`.

### Run with PostgreSQL

Set the following environment variables (or override `application.yml`):

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/task_manager
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
./gradlew bootRun
```

### Build & test

```bash
./gradlew build           # compile + test + assemble
./gradlew test            # run tests only
./gradlew jacocoTestReport # coverage report
```

---

## API Overview

| Method | Endpoint             | Description                          | Auth |
|--------|----------------------|--------------------------------------|------|
| POST   | `/api/login`         | Authenticate and obtain a JWT        | —    |
| POST   | `/api/users`         | Register a new user                  | —    |
| GET    | `/api/users`         | List all users                       | ✔    |
| GET    | `/api/users/{id}`    | Get a user                           | ✔    |
| PUT    | `/api/users/{id}`    | Update a user                        | ✔    |
| DELETE | `/api/users/{id}`    | Delete a user                        | ✔    |
| GET    | `/api/tasks`         | List tasks (with filters)            | ✔    |
| GET    | `/api/tasks/{id}`    | Get a single task                    | ✔    |
| POST   | `/api/tasks`         | Create a task                        | ✔    |
| PUT    | `/api/tasks/{id}`    | Update a task                        | ✔    |
| DELETE | `/api/tasks/{id}`    | Delete a task                        | ✔    |
| *      | `/api/task_statuses` | CRUD on task statuses                | ✔    |
| *      | `/api/labels`        | CRUD on labels                       | ✔    |

Full interactive docs: **`/swagger-ui.html`**

### Authentication example

```bash
curl -X POST https://java-project-99-pu4a.onrender.com/api/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "qwerty"}'
```

Use the returned JWT in the `Authorization` header for subsequent requests:

```bash
curl https://java-project-99-pu4a.onrender.com/api/tasks \
  -H "Authorization: Bearer <your-token>"
```

### Filtering tasks example

```bash
GET /api/tasks?titleCont=bug&assigneeId=2&status=in_progress&labelId=5
```

---

## Security

- Passwords are hashed with **BCrypt** (Spring Security `PasswordEncoder`).
- Sessions are **stateless** — every authenticated request must carry a JWT in the `Authorization` header.
- `verification-metadata.xml` enables SHA-256 + PGP integrity checks for every Maven dependency, mitigating supply-chain attacks.
- CSRF protection is intentionally disabled because the API is stateless and uses bearer-token authentication rather than cookie-based sessions (the classic CSRF attack vector).

---

## Live Demo

https://java-project-99-pu4a.onrender.com

---
