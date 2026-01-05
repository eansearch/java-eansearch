# EANSearch

Java class for EAN, GTIN, UPC and ISBN lookup and validation using the EAN-Search.org API.

Requirements
- Java (JDK)
- Gradle (project includes a minimal wrapper shim)

## Running the example

Environment
- `EAN_SEARCH_API_TOKEN` - API token (required for the example)

Build
```bash
chmod +x ./gradlew
./gradlew clean build
```

Run example
```bash
export EAN_SEARCH_API_TOKEN="<your-token>"
# Basic example: prints a barcode lookup and a short product list
./gradlew run
```

Notes
- The example reads the token from the environment and will exit with code 1 if not set.
- The Gradle wrapper in this repository is a minimal shim. If you prefer, install Gradle locally and run `gradle wrapper` to generate a full wrapper.

## Using the EANSearch class

```java
package com.relaxedcommunications.eansearch;

import java.util.List;

public class Example {
    public static void main(String[] args) {
        String token = System.getenv("EAN_SEARCH_API_TOKEN");
        if (token == null) {
            System.err.println("Set EAN_SEARCH_API_TOKEN env var");
            System.exit(1);
        }
        EANSearch client = new EANSearch(token);

        String sampleEan = "5099750442227";
        String sampleIsbn = "1119578884"; // ISBN-10

        // 1) barcodeLookup
        System.out.println("-- barcodeLookup --");
        ProductFull p = client.barcodeLookup(sampleEan, 1);
        if (p != null) {
            System.out.println(p.ean + " → " + p.name + " (category: " + p.categoryName + ", google category Id: " + p.googleCategoryId + ") issued in " + p.issuingCountry);
        } else {
            System.out.println("barcodeLookup: Not found");
        }

        // 2) isbnLookup (delegates to barcode-lookup)
        System.out.println("-- isbnLookup --");
        ProductFull book = client.isbnLookup("1119578884");
        System.out.println(book == null ? "isbnLookup: Not found" : (sampleIsbn + " → " + book.name));

        // 3) verifyChecksum
        System.out.println("-- verifyChecksum --");
        boolean ok = client.verifyChecksum(sampleEan);
        System.out.println(sampleEan + " checksum valid: " + ok);
        boolean bad = client.verifyChecksum(sampleEan + "1");
        System.out.println(sampleEan + "1 checksum valid: " + bad);

        // 4) productSearch
        System.out.println("-- productSearch (exact match) --");
        List<Product> products = client.productSearch("Bananaboat", 99, 0);
        for (Product q : products) {
            System.out.println(q.ean + " → " + q.name + " (" + q.categoryName + ")");
        }

        // 5) similarProductSearch
        System.out.println("-- similarProductSearch --");
        List<Product> similar = client.similarProductSearch("apple iphone withextratokenthatnevermatchesexact", 1);
        for (Product r : similar) {
            System.out.println(r.ean + " → " + r.name);
        }

        // 6) categorySearch (see appendix C in the API docs for category IDs)
        System.out.println("-- categorySearch --");
        List<Product> cat = client.categorySearch(45, "Bananaboat");
        for (Product c : cat) {
            System.out.println(c.ean + " → " + c.name + " (" + c.categoryName + ")");
        }

        // 7) barcodePrefixSearch
        System.out.println("-- barcodePrefixSearch --");
        List<Product> prefix = client.barcodePrefixSearch("5099750442");
        for (Product q : prefix) {
            System.out.println(q.ean + " → " + q.name + " (" + q.categoryName + ")");
        }

        // 8) issuingCountryLookup
        System.out.println("-- issuingCountryLookup --");
        String country = client.issuingCountryLookup(sampleEan);
        System.out.println(sampleEan + " issuing country: " + country);

        // 9) barcodeImage
        System.out.println("-- barcodeImage --");
        String img = client.barcodeImage(sampleEan, 300, 100);
        System.out.println("HTML: <img src=\"data:image/png;base64," + img + "\"");       
    }
}
```
