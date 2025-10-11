# Logi-Neko Backend

This is the backend API server for the Logi-Neko platform, designed for children's study activities.

## Features

- RESTful API for platform features
- Built with Spring Boot for robust, scalable service
- Authentication and authorization powered by [Keycloak](https://www.keycloak.org/)
- OAuth2/JWT authentication using Spring Security
- Dockerized for easy deployment (`docker-compose.yml`)
- Kafka and Zookeeper integration for messaging/events
- API documentation via Swagger/OpenAPI annotations
- WebSocket support for real-time notifications and chat
- Uses OpenFeign for easy HTTP client integration
- Database integration (PostgreSQL configured for Keycloak)
- Modular service and repository architecture

## Technologies Used

- **Spring Boot**
- **Spring Security** (OAuth2, JWT)
- **Keycloak** (Identity & Access Management)
- **OpenFeign** (Declarative REST client)
- **Kafka & Zookeeper** (Messaging)
- **Swagger/OpenAPI** (API docs)
- **Docker & Docker Compose**
- **Lombok** (for DTOs and models)
- **PostgreSQL** (database for Keycloak)
- **WebSocket** (real-time messaging)
- **JUnit** (testing, not shown in limited results)
- **SLF4J** (logging)

## Getting Started

### Prerequisites

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- `.env` file with required environment variables (see below)

### Environment Variables

Create a `.env` file in the root directory. Example contents:

```env
DB_HOST=your_database_host
DB_PORT=your_database_port
DB_USER=your_database_user
DB_PASS=your_database_password
SPRING_PROFILES_ACTIVE=dev
# Add other required variables here
```

### Running the Server

1. Build and start the services with Docker Compose:

   ```bash
   docker compose up --build
   ```

2. The API server will be available at `http://localhost:8081` by default.
3. Keycloak will be available at `http://localhost:8080`.

## API Documentation

- API endpoints and usage details coming soon.
- (Optionally, add documentation link or OpenAPI/Swagger info here.)

## Contributing

Contributions are welcome! Please open issues or pull requests for bug fixes, features, or improvements.

## License

(Add your license here if applicable.)

---

**Note:**  
Some technology details are based on a limited set of code search results. [View more results on GitHub.](https://github.com/Logi-Neko/back-end/search)
