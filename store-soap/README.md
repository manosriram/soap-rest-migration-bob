# Store Service — SOAP (legacy) & REST (modernized)

A self-contained **Java 8 / JAX-WS** SOAP server exposing **15 service operations** for a simple product & order store.  
No application server is required — it runs on the built-in JAX-WS HTTP endpoint.

---

## Project layout

```
store-soap/
├── pom.xml
└── src/
    ├── main/java/com/example/soap/
    │   ├── model/
    │   │   ├── Product.java          – product data model
    │   │   └── Order.java            – order data model
    │   ├── service/
    │   │   ├── StoreService.java     – SEI (service endpoint interface)
    │   │   └── StoreServiceImpl.java – service implementation
    │   └── server/
    │       └── SoapServer.java       – main class / server launcher
    └── test/java/com/example/soap/service/
        └── StoreServiceImplTest.java – JUnit 4 tests (one per operation)
```

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java (JDK)  | 8 or 11 |
| Maven       | 3.6+    |

Check your versions:

```bash
java -version
mvn  -version
```

---

## Build

```bash
# Compile and run all tests
mvn clean verify

# Or just compile (skip tests)
mvn clean package -DskipTests
```

A fat-jar is produced at `target/store-soap-1.0-SNAPSHOT.jar`.

---

## Run the SOAP server

```bash
java -jar target/store-soap-1.0-SNAPSHOT.jar
```

Expected output:

```
==============================================
  Starting Store SOAP Service...
==============================================
  Service published at : http://localhost:8080/store
  WSDL available at    : http://localhost:8080/store?wsdl
==============================================
  Press Ctrl+C to stop.
```

Open the WSDL in a browser or with `curl`:

```bash
curl http://localhost:8080/store?wsdl
```

---

## Run the tests

```bash
mvn test
```

Tests are verbose — every test prints a banner, its inputs, and its result to stdout before asserting.  
The Surefire plugin is configured with `useFile=false` so all output appears in the terminal.

---

## The 15 service operations

| # | Operation              | Description                                      |
|---|------------------------|--------------------------------------------------|
| 1 | `sayHello`             | Returns a personalised greeting                  |
| 2 | `addProduct`           | Adds a new product to the in-memory catalogue    |
| 3 | `getProduct`           | Retrieves a product by id                        |
| 4 | `listProducts`         | Returns all products                             |
| 5 | `updateProductPrice`   | Changes the price of an existing product         |
| 6 | `deleteProduct`        | Removes a product from the catalogue             |
| 7 | `placeOrder`           | Creates an order and deducts stock               |
| 8 | `getOrder`             | Retrieves an order by id                         |
| 9 | `listOrders`           | Returns all orders                               |
| 10| `cancelOrder`          | Cancels an order and restores stock              |
| 11| `calculateDiscount`    | Applies a percentage discount to a price         |
| 12| `convertCurrency`      | Multiplies an USD amount by a conversion rate    |
| 13| `checkStock`           | Checks whether enough stock is available         |
| 14| `applyTax`             | Adds a tax percentage to a price                 |
| 15| `generateInvoice`      | Returns a formatted invoice string for an order  |

---

## Calling the service with curl (example)

The service uses SOAP 1.1 / Document-Literal style.  
Save the following snippet as `sayHello.xml` and post it:

```xml
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:tns="http://example.com/soap/store">
  <soapenv:Header/>
  <soapenv:Body>
    <tns:sayHello>
      <name>Alice</name>
    </tns:sayHello>
  </soapenv:Body>
</soapenv:Envelope>
```

```bash
curl -s -X POST http://localhost:8080/store \
     -H "Content-Type: text/xml;charset=UTF-8" \
     -H "SOAPAction: sayHello" \
     -d @sayHello.xml
```

---

## Technology choices

| Choice | Reason |
|--------|--------|
| Java 8 | "older version" as requested; widely supported |
| JAX-WS 2.3.5 (RI) | Standard Java SOAP stack, available as a Maven dependency; no servlet container needed |
| JUnit 4 | Mature, simple, well-supported |
| Maven Shade | Produces a single runnable fat-jar |

---

## REST service (`store-rest/`)

The `store-rest/` directory contains the modernized **Java 17 / Spring Boot 3** REST
equivalent of the SOAP service above. All 15 operations are preserved; the protocol
is now JSON over HTTP.

### Prerequisites

| Requirement | Version |
|-------------|---------|
| Java (JDK)  | 17+     |
| Maven       | 3.6+    |

### Build

```bash
cd store-rest
mvn clean package
```

Produces `store-rest/target/store-rest-1.0-SNAPSHOT.jar`.

### Run the REST server

```bash
cd store-rest
java -jar target/store-rest-1.0-SNAPSHOT.jar
```

Server starts on `http://localhost:8080`. You should see Spring Boot's startup banner
followed by:

```
Started StoreApplication in X.XXX seconds
```

### Run the tests

```bash
cd store-rest
mvn test
```

80 tests across 5 test classes (service unit tests + MockMvc controller tests).
All must show `BUILD SUCCESS`.

### Quick-start curl sequence

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

### REST API reference

| Resource | Method | Path | Description |
|----------|--------|------|-------------|
| Products | POST | `/api/v1/products` | Add product |
| | GET | `/api/v1/products` | List all products |
| | GET | `/api/v1/products/{id}` | Get product by id |
| | PATCH | `/api/v1/products/{id}/price` | Update price |
| | DELETE | `/api/v1/products/{id}` | Delete product |
| | GET | `/api/v1/products/{id}/stock?requested=n` | Check stock |
| Orders | POST | `/api/v1/orders` | Place order |
| | GET | `/api/v1/orders` | List all orders |
| | GET | `/api/v1/orders/{id}` | Get order by id |
| | DELETE | `/api/v1/orders/{id}` | Cancel order |
| | GET | `/api/v1/orders/{id}/invoice` | Generate invoice |
| Utils | GET | `/api/v1/utils/hello?name=` | Greeting |
| | GET | `/api/v1/utils/discount?price=&percent=` | Calculate discount |
| | GET | `/api/v1/utils/currency?amountUSD=&rate=` | Convert currency |
| | GET | `/api/v1/utils/tax?price=&taxRate=` | Apply tax |
