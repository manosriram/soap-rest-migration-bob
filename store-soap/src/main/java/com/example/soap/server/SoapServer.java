package com.example.soap.server;

import com.example.soap.service.StoreServiceImpl;

import javax.xml.ws.Endpoint;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Standalone SOAP server entry-point.
 *
 * Publishes the StoreService on http://localhost:8080/store using the
 * built-in JAX-WS HTTP server (no application server needed).
 *
 * Run with:
 *   mvn package -q
 *   java -jar target/soap-server-1.0-SNAPSHOT.jar
 *
 * Then open the WSDL at:
 *   http://localhost:8080/store?wsdl
 *
 * On Java 9+ the JAX-WS RI WrapperBeanGenerator needs reflective access to
 * java.lang internals. If the required --add-opens flags are absent this class
 * re-launches itself with those flags automatically.
 */
public class SoapServer {

    /** Sentinel system property set before the re-exec so we don't loop. */
    private static final String RELAUNCHED_PROP = "soap.server.relaunched";

    public static final String ADDRESS = "http://localhost:8080/store";

    /** All 15 operations exposed by the service, with their SOAPAction values. */
    private static final String[][] OPERATIONS = {
        { "sayHello",           "Greet a caller by name" },
        { "addProduct",         "Add a new product to the catalogue" },
        { "getProduct",         "Retrieve a product by id" },
        { "listProducts",       "List all products in the catalogue" },
        { "updateProductPrice", "Update the price of an existing product" },
        { "deleteProduct",      "Remove a product from the catalogue" },
        { "placeOrder",         "Place an order for a product" },
        { "getOrder",           "Retrieve an order by id" },
        { "listOrders",         "List all orders" },
        { "cancelOrder",        "Cancel an existing order" },
        { "calculateDiscount",  "Calculate a discounted price" },
        { "convertCurrency",    "Convert a USD amount using a given rate" },
        { "checkStock",         "Check whether a product has sufficient stock" },
        { "applyTax",           "Apply a tax rate to a price" },
        { "generateInvoice",    "Generate a formatted invoice for an order" },
    };

    public static void main(String[] args) throws Exception {
        // On Java 9+ the JAX-WS RI needs --add-opens to generate wrapper beans
        // at runtime. If the sentinel property is absent we re-exec with the
        // required flags so the user never has to add them manually.
        if (System.getProperty(RELAUNCHED_PROP) == null && isJava9OrLater()) {
            relaunch(args);
            return; // parent process exits; child takes over
        }

        System.out.println("==============================================");
        System.out.println("  Starting Store SOAP Service...");
        System.out.println("==============================================");

        StoreServiceImpl implementor = new StoreServiceImpl();
        Endpoint endpoint = Endpoint.publish(ADDRESS, implementor);

        System.out.println("  Service published at : " + ADDRESS);
        System.out.println("  WSDL available at    : " + ADDRESS + "?wsdl");
        System.out.println("==============================================");
        printOperations();
        System.out.println("==============================================");
        System.out.println("  Press Ctrl+C to stop.");

        // Keep the JVM alive
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n  Stopping service...");
            endpoint.stop();
            System.out.println("  Service stopped. Goodbye.");
        }));

        Thread.currentThread().join();
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private static boolean isJava9OrLater() {
        try {
            return Runtime.class.getMethod("version") != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Re-executes this JVM process with the --add-opens flags that
     * JAX-WS RI requires on Java 9+.
     */
    private static void relaunch(String[] args) throws Exception {
        String javaExe = System.getProperty("java.home")
                + File.separator + "bin" + File.separator + "java";

        // Locate the jar we're running from (works for fat-jars)
        String classPath = System.getProperty("java.class.path");

        List<String> cmd = new ArrayList<>();
        cmd.add(javaExe);
        // flags required by JAX-WS RI WrapperBeanGenerator on Java 9+
        cmd.add("--add-opens=java.base/java.lang=ALL-UNNAMED");
        cmd.add("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED");
        cmd.add("--add-opens=java.base/java.util=ALL-UNNAMED");
        cmd.add("-D" + RELAUNCHED_PROP + "=true");
        cmd.add("-cp");
        cmd.add(classPath);
        cmd.add(SoapServer.class.getName());
        for (String arg : args) cmd.add(arg);

        Process proc = new ProcessBuilder(cmd)
                .inheritIO()
                .start();
        System.exit(proc.waitFor());
    }

    private static void printOperations() {
        System.out.println("  Available operations  (" + OPERATIONS.length + "):");
        System.out.println();
        for (int i = 0; i < OPERATIONS.length; i++) {
            String name = OPERATIONS[i][0];
            String desc = OPERATIONS[i][1];
            System.out.printf("  %2d. POST %s%n", i + 1, ADDRESS);
            System.out.printf("      SOAPAction : \"%s\"%n", name);
            System.out.printf("      Description: %s%n", desc);
            if (i < OPERATIONS.length - 1) System.out.println();
        }
    }
}
