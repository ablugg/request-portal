# Request Portal

A self-service request portal: customers submit a service request and track it with a
code; staff see the queue and move requests through their lifecycle.

Built with **Java 21**, **Spring Boot 4**, **PostgreSQL**, **Flyway**, and **Spring Security**.
The frontend is **React**, **TypeScript**, **Next.js**, and **Tailwind CSS**.

## Run it

### Prerequisites

- JDK 17 or newer and Maven
- Docker (for PostgreSQL)
- Node.js 18+ and npm (for the frontend)

On a Mac with Homebrew:

```bash
brew install maven node docker
```

### Start the backend

```bash
docker compose up -d            # starts PostgreSQL
mvn test                        # compiles and runs the 17 automated tests
mvn spring-boot:run             # starts the API on http://localhost:8080
```

The first run downloads Spring Boot and its dependencies, which takes a minute or two.
Flyway runs the database migrations automatically on startup.

### Start the frontend

```bash
cd frontend
npm install
npm run dev                     # starts the UI on http://localhost:3000
```

## Try it

Open **http://localhost:3000** to use the UI, or use curl:

```bash
# Submit a request (public, no auth required)
curl -i -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{"category":"TECHNICAL_ISSUE","title":"Cannot reset my password","description":"The reset email never arrives.","contactEmail":"sam@example.com"}'

# Track it (replace the code with yours)
curl http://localhost:8080/api/requests/SR-XXXXXXXX

# Staff: list the queue (requires auth)
curl -u admin:admin "http://localhost:8080/api/staff/requests?status=SUBMITTED"

# Staff: move the request along
curl -u admin:admin -X PATCH http://localhost:8080/api/staff/requests/SR-XXXXXXXX/status \
  -H "Content-Type: application/json" -d '{"status":"IN_PROGRESS"}'

# See validation errors
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{"category":"BILLING","title":"","description":"x","contactEmail":"nope"}'
```

## Endpoints

| Method | Path | Auth | What it does |
|---|---|---|---|
| POST | `/api/requests` | Public | Submit a request; returns a tracking code |
| GET | `/api/requests/{reference}` | Public | Track a request by its code |
| GET | `/api/staff/requests?status=` | Staff | List requests, optionally by status |
| PATCH | `/api/staff/requests/{reference}/status` | Staff | Move a request to a new status |

Staff endpoints require HTTP Basic authentication. A default account (`admin` / `admin`) is
seeded on first run.

## How the code is organised

### Backend

A request travels through three layers, top to bottom:

| Layer | Files | Job |
|---|---|---|
| Controller | `PublicRequestController`, `StaffRequestController` | Receives HTTP, checks the input, returns JSON |
| Service | `ServiceRequestService` | The rules: create a request, change its status |
| Repository | `ServiceRequestRepository` | Reads and writes the database |

Supporting pieces, all under `src/main/java/com/github/ablugg/requestportal/`:

- `request/ServiceRequest` is the entity: one object per database row.
- `request/RequestStatus` holds the lifecycle and which moves are allowed.
- `request/CreateRequest` and `UpdateStatusRequest` are the incoming JSON, with validation rules.
- `request/PublicRequestView` and `StaffRequestView` are the outgoing JSON. The public one leaves out the description and contact email.
- `request/ReferenceGenerator` makes the random tracking codes.
- `common/ApiExceptionHandler` turns errors into consistent JSON responses.
- `config/SecurityConfig` configures Spring Security: public endpoints are open, staff endpoints require the STAFF role.
- `config/DataSeeder` creates the default admin account on first run.
- `staff/StaffUser` and `StaffUserDetailsService` handle staff authentication against the database.

Database migrations live in `src/main/resources/db/migration/` and are run by Flyway on startup.

### Frontend

The frontend is a Next.js app in the `frontend/` directory:

| Route | Who | What |
|---|---|---|
| `/` | Customer | Submit a request, get a tracking code |
| `/track` | Customer | Enter tracking code, see status |
| `/staff` | Staff | Filter queue by status, expand details, change status |

Suggested reading order: `RequestStatus`, `ServiceRequest`, `ServiceRequestService`,
`PublicRequestController`, then `RequestApiTest`.

The requirements are in [docs/user-stories.md](docs/user-stories.md).

## Roadmap

1. ~~REST API with tests~~
2. ~~PostgreSQL with Flyway migrations, staff login with Spring Security, Docker Compose~~
3. ~~React and TypeScript front end~~
4. CI/CD pipeline, cloud deployment, and a UAT checklist

## Known limits

- The default admin password should be changed in production.
- The frontend does not yet prompt for staff credentials (it relies on the browser's built-in HTTP Basic dialog).
