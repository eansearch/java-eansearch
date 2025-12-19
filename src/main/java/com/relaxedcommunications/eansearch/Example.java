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
        ProductFull p = client.barcodeLookup("5099750442227", 1);
        if (p != null) {
            System.out.println(p.ean + " is " + p.name
                + " category " + p.categoryName + " google category ID " + p.googleCategoryId
                + ", issued in " + p.issuingCountry);
        } else {
            System.out.println("Not found");
}

        System.out.println("Products for 'Bananaboat':");
        int page = 0;
        List<Product> list;
        do {
            list = client.productSearch("Bananaboat", 99, page);
            for (Product q : list) {
                System.out.println(q.ean + " is " + q.name
                    + " category " + q.categoryName
                    + ", issued in " + q.issuingCountry);
            }
        } while (list.size() >= 10);
        
    }
}