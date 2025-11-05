# RecruitEdge Backend (Java)

Spring Boot service powering the RecruitEdge candidate–recruiter marketplace. The API manages authentication, recruiter/candidate onboarding, job postings, applications, and resume storage backed by Azure Blob Storage.

## Tech Stack
- Java 17, Spring Boot 3.5
- Spring Web, Spring Data JPA, Spring Security, Spring Validation
- SQL Server (DDL managed outside the app; `spring.jpa.hibernate.ddl-auto=none`)
- Azure Blob Storage for resume files
- Spring Mail (SMTP) for transactional email
- JWT (io.jsonwebtoken 0.9.1) for stateless auth

## Architecture Overview
- **Domain model:**
  - `User` is the root entity (string `userId`) with `Role`, status flags, and contact info.
  - `Candidate`, `Recruiter`, and `Employer` extend user data via one-to-one relationships.
  - `Job` links to `Employer`, `Location`, and optional `Industry` set.
  - `Application` references numeric `jobId` and `candidateId` values from the database.
  - `Resume` belongs to a candidate and stores Azure blob paths plus default-state metadata.
- **Services:**
  - `AuthService` handles signup/login/reset flows, generates custom user IDs, enforces recruiter email suffixes, and emits welcome/reset emails via `EmailService`.
  - `RecruiterService` creates both `Employer` and `Recruiter` rows, allocates sequential five-digit employer numbers (`EmployerRepository.findMaxEmployerNumber()`), and flips `User.profileCompleted`.
  - `CandidateService` manages candidate profile completion, validating DOB and social links while supporting partial updates.
  - `JobService` posts, updates, and deletes jobs for recruiters with completed profiles, performs ownership checks, and reuses `Location` rows by city/state/zip.
  - `ApplicationService` prevents duplicate job applications, sets default status to 1 (Applied), and allows status updates or withdrawals.
  - `ResumeService` limits each candidate to three resumes, keeps a single default, stores files in Azure Blob via `AzureBlobService`, and issues SAS download links.

## Security
- Stateless JWT security with `SecurityConfig` and `JwtAuthenticationFilter`.
  - JWT subject is the user email, so `Authentication#getName()` returns the email; controllers translate to `userId` via `UserRepository.findByEmail` before enforcing ownership.
- `@PreAuthorize` guards methods; align new endpoints with the rules declared in `SecurityConfig`’s request matchers.
- `AuthController.logout` blacklists tokens in `JwtBlacklistService`, but the filter does not yet reject those tokens—rely on token TTL until the blacklist check is wired in.

## Storage and Integrations
- **Database:** Azure SQL Server. Apply schema changes with `database_setup.sql` and `database_migration.sql`; Hibernate does not manage DDL.
- **Azure Blob:** `AzureBlobConfig` builds a `BlobServiceClient` from `azure.storage.*` properties and auto-creates the resume container. Provide `AZURE_STORAGE_KEY` at runtime.
- **Email:** Configure SMTP via Spring Mail (`spring.mail.*`). Keep secrets out of git by setting environment overrides like `EMAIL_PASSWORD`.

## Project Layout
```
src/main/java/com/careermatch/pamtenproject/
├── controller/          # REST controllers (Auth, Candidate, Recruiter, Job, Resume, etc.)
├── dto/                 # Request/response DTOs with validation annotations
├── exception/           # Custom exceptions + GlobalExceptionHandler
├── interceptor/         # RequestLoggingInterceptor adds per-request UUID logging
├── model/               # JPA entities
├── repository/          # Spring Data repositories
├── security/            # JWT utilities, filters, security config
└── service/             # Business logic services listed above
```

## Configuration
- Base settings live in `src/main/resources/application.properties` (SQL connection, JWT defaults, Azure storage, mail, logging).
- Override sensitive values with environment variables: `DB_PASSWORD`, `JWT_SECRET`, `EMAIL_PASSWORD`, `AZURE_STORAGE_KEY`.
- CORS is limited to `http://localhost:3000` by default (`WebConfig`). Update there when adding new origins.

## Running Locally
```powershell
./mvnw spring-boot:run

# Or package then run
./mvnw clean package
java -jar target/pamtenproject-0.0.1-SNAPSHOT.jar
```
- Ensure the SQL Server connection string in `application.properties` is reachable from your environment.
- Seed roles, genders, and status tables with the provided SQL scripts if your database is empty.

### Tests
```powershell
./mvnw clean verify
```
Current tests only boot the Spring context; add unit/integration coverage under `src/test/java` as features evolve.

## Key API Endpoints
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health check: `GET /api/test/v1/health` (touches repositories to confirm DB access)
- Auth: `/api/auth/v1/login`, `/register`, `/forgot-password`, `/reset-password`
- Candidate profile: `/api/candidate/v1/profile`
- Recruiter profile: `/api/recruiter/v1/profile`, `/complete-profile`
- Job management: `/api/jobs/v1/post`, `/api/jobs/v1/{jobId}` (PUT/DELETE)
- Applications: `/api/applications/v1/apply`, `/candidate/{candidateId}`, `/job/{jobId}`
- Resumes: `/api/candidate/resumes/v1/upload`, `/candidate`, `/{resumeId}/download`

## Logging and Error Handling
- `RequestLoggingInterceptor` writes a UUID-backed entry for each request and completion/error.
- `GlobalExceptionHandler` centralizes error responses with a consistent JSON shape. Use the custom exceptions under `exception/` instead of raw runtime errors.

## Known Gaps / TODOs
- JWT blacklist is not yet enforced by the filter layer.
- Resume APIs still rely on email query parameters; migrate to JWT-derived identity once the client supports it.
- Additional automated tests and rate-limiting hooks are pending.

## Contributing
1. Create a branch.
2. Make changes and update/add tests.
3. Run `./mvnw clean verify`.
4. Submit a PR referencing the relevant ticket.

For AI assistant conventions and deeper architectural notes, see `.github/copilot-instructions.md`.
