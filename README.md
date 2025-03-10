# CarsApp API

The **CarsApp API** is a Spring Boot-based RESTful API designed to manage cars, engines, and users. It supports CRUD operations for cars and engines, user authentication with JWT, and features like buying/selling cars, photo uploads, and search functionality. The API is built with scalability and security in mind, leveraging modern Java and Spring technologies.

---

## Technologies Used

- **Java 21**: The primary programming language.
- **Spring Boot**: Framework for building the application.
- **Spring Data JPA**: For database interaction and ORM.
- **Spring Security**: For authentication and authorization.
- **JWT (JSON Web Tokens)**: For secure user authentication.
- **Jakarta Validation**: For request validation.
- **Lombok**: For reducing boilerplate code.
- **PostgreSQL**: The relational database used for data storage.
- **Gradle**: The build tool for dependency management.
- **Liquibase**: For database migration and version control.
- **AWS SDK**: For handling file uploads to AWS S3.

---

## Database Schema

The database schema consists of the following tables:

| Table Name   | Columns                                                                 |
|--------------|-------------------------------------------------------------------------|
| `app_user`   | `id (bigint)`, `username (varchar(100))`, `password (varchar)`, `balance_in_cents (bigint)` |
| `car`        | `id (bigint)`, `model (varchar(255))`, `year (int)`, `is_driveable (boolean)`, `engine_id (bigint)`, `price_in_cents (bigint)`, `photo_url (varchar)` |
| `engine`     | `id (bigint)`, `horse_power (int)`, `capacity (numeric(10,2))`         |
| `role`       | `id (bigint)`, `name (varchar(50))`                                    |
| `user_car`   | `user_id (bigint)`, `car_id (bigint)`                                  |
| `user_role`  | `user_id (bigint)`, `role_id (bigint)`                                 |

---

## REST API Endpoints

### Authentication
| Method | Endpoint         | Description                          |
|--------|------------------|--------------------------------------|
| POST   | `/auth/login`    | Authenticate a user and return a JWT. |

### Engines
| Method | Endpoint              | Description                          |
|--------|-----------------------|--------------------------------------|
| GET    | `/engines`            | Get a paginated list of engines.     |
| GET    | `/engines/{id}`       | Get details of a specific engine.    |
| POST   | `/engines`            | Create a new engine (Admin only).    |
| PUT    | `/engines/{id}`       | Update an engine (Admin only).       |
| DELETE | `/engines/{id}`       | Delete an engine (Admin only).       |

### Cars
| Method | Endpoint                     | Description                          |
|--------|------------------------------|--------------------------------------|
| GET    | `/cars`                      | Get a paginated list of cars.        |
| GET    | `/cars/for-sale`             | Get a list of cars for sale.         |
| GET    | `/cars/search`               | Search cars by model name.           |
| POST   | `/cars/{carId}/list-for-sale`| List a car for sale.                 |
| POST   | `/cars/{carId}/purchase`     | Purchase a car.                      |
| POST   | `/cars`                      | Add a new car with a photo (Admin only). |
| PUT    | `/cars/{id}`                 | Update a car (Admin only).           |
| DELETE | `/cars/{id}`                 | Delete a car (Admin only).           |
| GET    | `/cars/{id}`                 | Get details of a specific car.       |

### Users
| Method | Endpoint              | Description                          |
|--------|-----------------------|--------------------------------------|
| POST   | `/users`              | Create a new user (Admin only).      |
| GET    | `/users`              | Get a list of all users (Admin only).|
| GET    | `/users/{username}`   | Get details of a specific user (Admin only). |

---

## Exception Handling

The API handles exceptions globally using `@ControllerAdvice`. Custom exceptions are used to provide meaningful error messages.

### Custom Exceptions
- **NotFoundException**: Thrown when a resource is not found.
- **InvalidLoginException**: Thrown when login credentials are invalid.
- **MethodArgumentNotValidException**: Handles validation errors and returns a structured error response.

### Error Response Format
json
{
"errorCode": "error-code",
"errorMessage": "Detailed error message"
}

## Validation Constraints

| Field | Constraints |
|-------|------------|
| **CarRequest.model** | `@NotBlank`, `@Size(max=20)` |
| **CarRequest.year** | `@ValidModelYear` (1940 to current year) |
| **CarRequest.engineId** | `@Positive` |
| **CarRequest.priceInCents** | `@Positive` |
| **EngineRequest.horsePower** | `@Positive` |
| **EngineRequest.capacity** | `@Positive` |
| **UserRequest.username** | `@NotBlank`, `@Size(min=5, max=20)` |
| **UserRequest.password** | `@NotBlank`, `@Size(min=8)` |
| **UserRequest.roleIds** | `@NotEmpty` |

## Running the Application

### Prerequisites
- Java 21
- PostgreSQL
- Gradle

### Steps
1. **Clone the repository:**
   ```sh
   git clone <repo-url>
   cd <repo-folder>
   ```
2. **Configure PostgreSQL database:**
    - Create a database in PostgreSQL.
    - Update `application.properties` or `application.yml` with your database credentials.
3. **Run the application:**
   ```sh
   ./gradlew bootRun
   ```
4. **Access the API:**
    - API will be available at `http://localhost:8080`
    - Use tools like Postman or cURL to interact with the API.