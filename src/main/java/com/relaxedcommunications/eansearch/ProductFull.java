package com.relaxedcommunications.eansearch;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Extended product information that includes Google category id in addition to
 * the base {@link Product} fields.
 */
public class ProductFull extends Product {
    /** Google product taxonomy category id if available. */
    public int googleCategoryId;

    public ProductFull() {
        super();
    }

    /**
     * Parse a JSON object into a {@link ProductFull} instance.
     *
     * @param node JSON object node
     * @return parsed ProductFull or {@code null} on invalid input
     */
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