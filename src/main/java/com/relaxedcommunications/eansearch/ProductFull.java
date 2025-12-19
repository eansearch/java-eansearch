package com.relaxedcommunications.eansearch;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Extended product information (adds Google category id).
 */
public class ProductFull extends Product {
    public int googleCategoryId;

    public ProductFull() {
        super();
    }

    public static ProductFull fromJson(JsonNode node) {
        if (node == null || !node.isObject()) return null;
        ProductFull pf = new ProductFull();
        pf.ean = node.path("ean").asText("");
        pf.name = node.path("name").asText("");
        pf.categoryId = node.path("categoryId").asInt(0);
        pf.categoryName = node.path("categoryName").asText("");
        pf.issuingCountry = node.path("issuingCountry").asText("");
        pf.googleCategoryId = node.path("googleCategoryId").asInt(0);
        return pf;
    }
}