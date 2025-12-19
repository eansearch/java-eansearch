# EANSearch

Java class for EAN, GTIN, UPC and ISBN lookup and validation using the EAN-Search.org API.

Requirements
- Java 21 (JDK)
- Gradle (project includes a minimal wrapper shim)

## Using the example

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
