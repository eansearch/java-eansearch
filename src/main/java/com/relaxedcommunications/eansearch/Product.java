package com.relaxedcommunications.eansearch;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Basic product information returned by product list endpoints.
 *
 * <p>Fields are intentionally package-public for simple data-holder usage and
 * populated from JSON responses using {@link #fromJson(com.fasterxml.jackson.databind.JsonNode)}.
 */
public class Product {
    /** EAN/UPC code. */
    public String ean;
    /** Human readable product name. */
    public String name;
    /** Numeric category id. */
    public int categoryId;
    /** Category name. */
    public String categoryName;
    /** ISO issuing country code. */
    public String issuingCountry;

    public Product() {}

    /**
     * Create a {@link Product} instance from a JSON node returned by the API.
     *
     * @param node JSON object node
     * @return parsed Product or {@code null} if the node is invalid
     */
    public static Product fromJson(JsonNode node) {
        if (node == null || !node.isObject()) return null;
        Product p = new Product();
        p.ean = node.path("ean").asText("");
        p.name = node.path("name").asText("");
        p.categoryId = node.path("categoryId").asInt(0);
        p.categoryName = node.path("categoryName").asText("");
        p.issuingCountry = node.path("issuingCountry").asText("");
        return p;
    }
}