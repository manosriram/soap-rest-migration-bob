# Store Service — SOAP (legacy)

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
