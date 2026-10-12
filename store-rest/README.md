# Store Service — REST (modernized)

A **Java 17 / Spring Boot 3** REST equivalent of the legacy SOAP store service.  
All 15 operations are preserved; the protocol is now JSON over HTTP.

---

## Project layout

```
store-rest/
├── pom.xml
└── src/
    ├── main/java/com/example/rest/
    │   ├── model/
    │   │   ├── Product.java          – product data model
    │   │   └── Order.java            – order data model
    │   ├── service/
    │   │   └── StoreService.java     – business logic
    │   └── controller/
    │       └── StoreController.java  – REST endpoints
    └── test/java/com/example/rest/
        └── ...                       – JUnit / MockMvc tests
```

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java (JDK)  | 17+     |
| Maven       | 3.6+    |

Check your versions:

```bash
java -version
mvn  -version
```

---

## Build

```bash
mvn clean package
```

Produces `target/store-rest-1.0-SNAPSHOT.jar`.

---

## Run the REST server

```bash
java -jar target/store-rest-1.0-SNAPSHOT.jar
```

Server starts on `http://localhost:8080`. You should see Spring Boot's startup banner followed by:

```
Started StoreApplication in X.XXX seconds
```

Once running, open your browser to **`http://localhost:8080`** to access the interactive **Store REST API Explorer UI**.

---

## Interactive Web UI & API Explorer

The service includes a built-in single-page web UI accessible at `http://localhost:8080/`:
- **📦 Products Catalog**: Add products, view inventory, check stock availability, update prices, and delete items.
- **🛒 Orders Manager**: Place orders with automatic inventory deduction, list orders, cancel orders with automatic stock restoral, and generate printable invoice receipts.
- **🧮 Utility Tools**: Interactive test tools for Greeting, Discount Calculator, Currency Converter, and Tax Calculator.
- **⚡ Live API Console**: Real-time request and response inspector displaying HTTP methods, URLs, status codes, latency, request payloads, and formatted JSON responses.
- **🌱 One-Click Catalog Seeder**: Quickly populate sample items and orders for demonstration.

---


## Run the tests

```bash
mvn test
```

80 tests across 5 test classes (service unit tests + MockMvc controller tests).  
All must show `BUILD SUCCESS`.

---

## Quick-start curl sequence

```bash
BASE=http://localhost:8080/api/v1

# 1 – Add a product
curl -s -X POST $BASE/products \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"Laptop","price":999.99,"quantity":10}'

# 2 – Place an order (2 units)
curl -s -X POST $BASE/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"customerName":"Alice","quantity":2}'

# 3 – Check stock was deducted (quantity should be 8)
curl -s $BASE/products/1

# 4 – Fetch the order (orderId auto-starts at 1000)
curl -s $BASE/orders/1000

# 5 – Generate the invoice
curl -s $BASE/orders/1000/invoice

# 6 – Cancel the order (stock restored to 10)
curl -s -X DELETE $BASE/orders/1000
```

---

## REST API reference

| Resource | Method | Path                                      | Description         |
|----------|--------|-------------------------------------------|---------------------|
| Products | POST   | `/api/v1/products`                        | Add product         |
|          | GET    | `/api/v1/products`                        | List all products   |
|          | GET    | `/api/v1/products/{id}`                   | Get product by id   |
|          | PATCH  | `/api/v1/products/{id}/price`             | Update price        |
|          | DELETE | `/api/v1/products/{id}`                   | Delete product      |
|          | GET    | `/api/v1/products/{id}/stock?requested=n` | Check stock         |
| Orders   | POST   | `/api/v1/orders`                          | Place order         |
|          | GET    | `/api/v1/orders`                          | List all orders     |
|          | GET    | `/api/v1/orders/{id}`                     | Get order by id     |
|          | DELETE | `/api/v1/orders/{id}`                     | Cancel order        |
|          | GET    | `/api/v1/orders/{id}/invoice`             | Generate invoice    |
| Utils    | GET    | `/api/v1/utils/hello?name=`               | Greeting            |
|          | GET    | `/api/v1/utils/discount?price=&percent=`  | Calculate discount  |
|          | GET    | `/api/v1/utils/currency?amountUSD=&rate=` | Convert currency    |
|          | GET    | `/api/v1/utils/tax?price=&taxRate=`       | Apply tax           |

---

## Technology choices

| Choice           | Reason                                                   |
|------------------|----------------------------------------------------------|
| Java 17          | LTS release; required by Spring Boot 3                   |
| Spring Boot 3    | Convention-over-configuration REST; embedded Tomcat      |
| JUnit 5 / MockMvc | Modern test stack bundled with `spring-boot-starter-test` |
| Maven            | Consistent with the sibling SOAP service                 |
