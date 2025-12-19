package com.relaxedcommunications.eansearch;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Basic product information.
 */
public class Product {
    public String ean;
    public String name;
    public int categoryId;
    public String categoryName;
    public String issuingCountry;

    public Product() {}

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