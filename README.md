# Request Portal

A self-service request portal: customers submit a service request and track it with a
code; staff see the queue and move requests through their lifecycle.

Stage 1 (this version) is the REST API, built with **Java 21** and **Spring Boot 4**.

## Run it

You need a JDK (17 or newer) and Maven. On a Mac with Homebrew, one command installs both:

```bash
brew install maven
mvn -v          # should print a Maven version and a Java version
```

Then, from this folder:

```bash
mvn test                # compiles and runs the 15 automated tests
mvn spring-boot:run     # starts the API on http://localhost:8080
```

The first run downloads Spring Boot and its dependencies, which takes a minute or two.
Data is held in an in-memory database, so it resets each time you stop the app.

## Try it

With the app running, in a second terminal:

```bash
# Submit a request. Copy the "reference" from the response.
curl -i -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{"category":"TECHNICAL_ISSUE","title":"Cannot reset my password","description":"The reset email never arrives.","contactEmail":"sam@example.com"}'

# Track it (replace the code with yours)
curl http://localhost:8080/api/requests/SR-XXXXXXXX

curl http://localhost:8080/api/requests/SR-GPYT59FC
# Staff: list the queue, then move the request along
curl "http://localhost:8080/api/staff/requests?status=SUBMITTED"
curl -X PATCH http://localhost:8080/api/staff/requests/SR-XXXXXXXX/status \
  -H "Content-Type: application/json" -d '{"status":"IN_PROGRESS"}'

# See validation errors
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{"category":"BILLING","title":"","description":"x","contactEmail":"nope"}'
```

## Endpoints

| Method | Path | Who | What it does |
|---|---|---|---|
| POST | `/api/requests` | Customer | Submit a request; returns a tracking code |
| GET | `/api/requests/{reference}` | Customer | Track a request by its code |
| GET | `/api/staff/requests?status=` | Staff | List requests, optionally by status |
| PATCH | `/api/staff/requests/{reference}/status` | Staff | Move a request to a new status |

## How the code is organised

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

Suggested reading order: `RequestStatus`, `ServiceRequest`, `ServiceRequestService`,
`PublicRequestController`, then `RequestApiTest`.

The requirements this stage implements are in [docs/user-stories.md](docs/user-stories.md).

## Roadmap

1. **REST API** with tests (this stage)
2. PostgreSQL with Flyway migrations, staff login with Spring Security, Docker Compose
3. React and TypeScript front end, built to WCAG 2.2 AA
4. CI/CD pipeline, cloud deployment, and a UAT checklist

## Known limits of stage 1

- The staff endpoints have no login yet. Run this on your own machine only.
- Data is not persisted between runs.
