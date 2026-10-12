# Features Roadmap — store-rest

Possible features to build on top of the existing Spring Boot REST service.

---

## Persistence

- **Relational DB (PostgreSQL / MySQL)** — Replace the in-memory lists with Spring Data JPA + Hibernate. Entities map directly to the existing `Product` and `Order` models. Data survives restarts.
- **Embedded DB for local dev (H2)** — Drop-in for development and testing; same JPA code, no external server needed. Switch to Postgres via `application.properties` for production.
- **Flyway / Liquibase migrations** — Version-controlled schema changes so the DB evolves safely alongside the code.
- **Soft deletes** — Instead of removing products/orders, mark them as `deleted=true` with a timestamp. Useful for audit trails and order history.
- **Optimistic locking** — Add a `@Version` field to `Product` to prevent lost updates when stock is decremented concurrently.

---

## UI

- **Admin dashboard (React / Vue)** — Single-page app served separately. Pages for product catalogue management, live order list, and stock levels. Talks to the existing `/api/v1` endpoints.
- **Storefront (Next.js)** — Customer-facing page to browse products, add to cart, and place orders. Could be a separate service or a static site hitting the REST API.
- **Invoice viewer** — Renders the plain-text invoice from `GET /api/v1/orders/{id}/invoice` as a styled HTML receipt with print/PDF export.
- **Stock alert badges** — UI highlights products with low stock (e.g. quantity < 5) in real time using polling or WebSocket push.

---

## API Enhancements

- **Pagination & sorting** — Add `page`, `size`, and `sort` query params to `GET /api/v1/products` and `GET /api/v1/orders` using Spring Data's `Pageable`.
- **Search & filtering** — Filter products by name, price range; filter orders by customer name or status.
- **Order status lifecycle** — Extend `Order` with a `status` field (`PENDING → CONFIRMED → SHIPPED → DELIVERED → CANCELLED`) and expose `PATCH /api/v1/orders/{id}/status`.
- **Bulk operations** — `POST /api/v1/products/batch` to add multiple products in one request; useful for catalogue imports.
- **Tasks / to-do endpoint** — `GET /api/v1/tasks` for internal admin task tracking (e.g. restock reminders, pending shipments).

---

## Auth & Security

- **Spring Security + JWT** — Protect write endpoints (`POST`, `PATCH`, `DELETE`) behind a JWT-authenticated role. Read endpoints can remain public.
- **Role-based access** — `ADMIN` role for product management; `CUSTOMER` role for placing and viewing own orders only.
- **API key support** — Simple key-based auth for machine-to-machine clients (e.g. a storefront calling the REST API).

---

## Observability

- **Spring Boot Actuator** — Expose `/actuator/health`, `/actuator/metrics`, and `/actuator/info` out of the box.
- **Structured logging (Logback + JSON)** — Machine-readable logs for aggregation in tools like Datadog or ELK.
- **Distributed tracing (OpenTelemetry)** — Trace requests end-to-end, especially useful once a UI and DB layer are added.
- **Prometheus + Grafana** — Scrape Actuator metrics and build dashboards for order throughput, stock levels, and latency.

---

## Notifications

- **Email receipts** — Send an order confirmation email via Spring Mail (SMTP / SendGrid) when `placeOrder` succeeds.
- **Webhook support** — Fire a configurable HTTP callback when an order is placed or cancelled; useful for integrating with external fulfilment systems.
- **Low-stock alerts** — Trigger a notification (email or webhook) when a product's quantity drops below a configured threshold.

---

## Developer Experience

- **OpenAPI / Swagger UI** — Add `springdoc-openapi` to auto-generate interactive API docs at `/swagger-ui.html`.
- **Docker Compose setup** — `docker-compose.yml` to spin up the service + Postgres + (optionally) the UI in one command.
- **Integration test suite** — `@SpringBootTest` tests against a real H2 database, covering the full HTTP → service → DB round-trip.
- **CI pipeline (GitHub Actions)** — Run `mvn verify` on every push; fail the build if any test breaks.
