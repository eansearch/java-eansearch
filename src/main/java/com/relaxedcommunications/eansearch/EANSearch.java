/*
 * Java class for EAN, GTIN, UPC and ISBN lookup and validation using the EAN-Search.org API.
 *
 * Copyright (c) 2025 Relaxed Communications GmbH <info@relaxedcommunications.com>
 *
 * License: MIT (https://opensource.org/licenses/MIT)
 */
package com.relaxedcommunications.eansearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Client for the EAN Search API (synchronous).
 *
 * <p>Provides convenience methods to call the remote API endpoints and parse responses into
 * {@link Product} and {@link ProductFull} objects. Typical usage:
 * <pre>
 *   EANSearch client = new EANSearch(token);
 *   ProductFull p = client.barcodeLookup("5099750442227", 1);
 * </pre>
 */
public class EANSearch {
    private static final int MAX_API_TRIES = 3;
    private static final String API_HOST = "https://api.ean-search.org/api";
    private final String token;
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public EANSearch(String token) {
        this.token = token;
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    /**
     * Perform a raw API call and return the JSON response body as string.
     *
     * @param params query parameters (without token/format)
     * @return JSON response body on success, or {@code null} on error
     */
    private String apiCall(String params) {
        return apiCall(params, 1);
    }

    private String apiCall(String params, int tries) {
        String target = API_HOST + "?" + params + "&token=" + token + "&format=json";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(target))
                .header("User-Agent", "java-eansearch/1.0")
                .GET()
                .build();
        try {
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 429 && tries <= MAX_API_TRIES) {
                return apiCall(params, tries + 1);
            }
            if (resp.statusCode() == 200) {
                return resp.body();
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    /**
     * Lookup product details by EAN/barcode.
     *
     * @param ean EAN/UPC code as string
     * @param language language id (0 = default)
     * @return {@link ProductFull} with extended product info, or {@code null} if not found/error
     */
    public ProductFull barcodeLookup(String ean, int language) {
        String res = apiCall("op=barcode-lookup&ean=" + ean + "&language=" + language);
        if (res == null) return null;
        try {
            JsonNode root = mapper.readTree(res);
            JsonNode node = root.isArray() && root.size() > 0 ? root.get(0) : root;
            return ProductFull.fromJson(node);
        } catch (IOException e) {
            return null;
        }
    }
    public ProductFull barcodeLookup(String ean) {
        return barcodeLookup(ean, 1);
    }


    /**
     * Lookup book information by ISBN (delegates to barcode-lookup).
     *
     * @param isbn ISBN string
     * @return {@link ProductFull} or {@code null}
     */
    public ProductFull isbnLookup(String isbn) {
        String res = apiCall("op=barcode-lookup&isbn=" + isbn);
        if (res == null) return null;
        try {
            JsonNode root = mapper.readTree(res);
            JsonNode node = root.isArray() && root.size() > 0 ? root.get(0) : root;
            return ProductFull.fromJson(node);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Verify EAN checksum.
     *
     * @param ean EAN code
     * @return {@code true} if checksum valid, {@code false} otherwise or on error
     */
    public boolean verifyChecksum(String ean) {
        String res = apiCall("op=verify-checksum&ean=" + ean);
        if (res == null) return false;
        try {
            JsonNode root = mapper.readTree(res);
            JsonNode val = root.isArray() && root.size() > 0 ? root.get(0).path("valid") : root.path("valid");
            String s = val.asText("");
            return "1".equals(s) || "true".equalsIgnoreCase(s);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Search products by name.
     *
     * @param name product name or partial name
     * @param onlyLanguage language id filter (0 = all)
     * @param page pagination page index (0-based)
     * @return list of {@link Product} (empty list if none or on error)
     */
    public List<Product> productSearch(String name, int onlyLanguage, int page) {
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (Exception e) {
            name = "";
        }
        String res = apiCall("op=product-search&name=" + name + "&language=" + onlyLanguage + "&page=" + page);
        return parseProductList(res);
    }

    public List<Product> productSearch(String name, int onlyLanguage) {
        return productSearch(name, onlyLanguage, 0);
    }

    public List<Product> productSearch(String name) {
        return productSearch(name, 99, 0);
    }

    /**
     * Search for similar products by name.
     *
     * @see #productSearch(String, int, int)
     */
    public List<Product> similarProductSearch(String name, int onlyLanguage, int page) {
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (Exception e) {
            name = "";
        }
        String res = apiCall("op=similar-product-search&name=" + name + "&language=" + onlyLanguage + "&page=" + page);
        return parseProductList(res);
    }

    public List<Product> similarProductSearch(String name, int onlyLanguage) {
        return similarProductSearch(name, onlyLanguage, 0);
    }

    public List<Product> similarProductSearch(String name) {
        return similarProductSearch(name, 99, 0);
    }

    /**
     * Search products within a category.
     *
     * @param category numeric category id
     */
    public List<Product> categorySearch(int category, String name, int onlyLanguage, int page) {
                try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (Exception e) {
            name = "";
        }
        String res = apiCall("op=category-search&category=" + category + "&name=" + name + "&language=" + onlyLanguage + "&page=" + page);
        return parseProductList(res);
    }

    public List<Product> categorySearch(int category, String name, int onlyLanguage) {
        return categorySearch(category, name, onlyLanguage, 0);
    }

    public List<Product> categorySearch(int category, String name) {
        return categorySearch(category, name, 99, 0);
    }

    /**
     * Search products by barcode prefix.
     */
    public List<Product> barcodePrefixSearch(String prefix, int language, int page) {
        String res = apiCall("op=barcode-prefix-search&prefix=" + prefix + "&language=" + language + "&page=" + page);
        return parseProductList(res);
    }

    public List<Product> barcodePrefixSearch(String prefix, int language) { 
        return barcodePrefixSearch(prefix, language, 0);
    }

    public List<Product> barcodePrefixSearch(String prefix) { 
        return barcodePrefixSearch(prefix, 1, 0);
    }

    /**
     * Return the issuing country for a given EAN code (ISO code string).
     */
    public String issuingCountryLookup(String ean) {
        String res = apiCall("op=issuing-country&ean=" + ean);
        if (res == null) return "";
        try {
            JsonNode root = mapper.readTree(res);
            JsonNode val = root.isArray() && root.size() > 0 ? root.get(0).path("issuingCountry") : root.path("issuingCountry");
            return val.asText("");
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Request a barcode image URL or data for the given EAN.
     *
     * @return encoded barcode string or empty on error
     */
    public String barcodeImage(String ean, int width, int height) {
        String res = apiCall("op=barcode-image&ean=" + ean + "&width=" + width + "&height=" + height);
        if (res == null) return "";
        try {
            JsonNode root = mapper.readTree(res);
            JsonNode val = root.isArray() && root.size() > 0 ? root.get(0).path("barcode") : root.path("barcode");
            return val.asText("");
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Parse a JSON product list response into {@link Product} objects.
     *
     * @param json raw JSON string returned by the API
     * @return parsed list (never {@code null})
     */
    private List<Product> parseProductList(String json) {
        List<Product> list = new ArrayList<>();
        if (json == null) return list;
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode arr = root.path("productlist");
            if (!arr.isArray()) return list;
            for (JsonNode n : arr) {
                Product p = Product.fromJson(n);
                if (p != null) list.add(p);
            }
        } catch (IOException ignored) {}
        return list;
    }
}
