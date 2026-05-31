# SDA-PRO

A SOC automation starter project with service-oriented backend components, shared domain/event models, database migration scripts, integration-test scaffolding, and a simple dashboard.

## Structure

- `docker-compose.yml` - local development environment
- `scripts/` - migration and integration-test assets
- `services/` - Spring Boot microservices
- `soc-dashboard/` - static web dashboard
- `shared/` - shared Java models and event definitions

## Quick start

1. Copy `.env` values as needed.
2. Run `docker compose up --build`.
3. Open `http://localhost:3000` for the dashboard.

## Notes

- `event-bus` is merged into `shared` as requested.
- Java services are scaffolded as minimal Spring Boot apps for quick expansion.
- The smoke test script validates the alert ingestion and incident management flow.
