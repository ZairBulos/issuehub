# IssueHub

IssueHub is a centralized issue intake and ticket management system for development projects.

## Project Structure

```text
/src
  /shared               ← Shared code (utilities)
  /modules
    /auth               ← Authentication module
       /domain          ← Entities, business rules (private)
       /application     ← Use cases, DTOs, input ports (public)
       /infrastructure  ← Repositories, adapters, DB (private)

    /developers         ← Developer management module
       /domain
       /application
       /infrastructure

    /notifications      ← Notifications module
       /domain
       /application
       /infrastructure

    /projects           ← Projects module
       /domain
       /application
       /infrastructure

    /tickets            ← Tickets module
       /domain
       /application
       /infrastructure
```

## Commands

### Testing

```shell
# Run unit tests only
mvn clean test

# Run unit + integration tests
mvn clean verify

# Run integration tests only
mvn failsafe:integration-test
```

###  Database

```shell
# Access the database
docker exec -it issuehub-postgres psql -U postgres -d issuehub_db
```