package com.ecom.dtos.requests;

import lombok.Data;

@Data
public  class ProductQuery {
     public enum SortBy {
        FEATURED("Featured"),
        BEST_SELLING("Best Selling"),
        ALPHABETICALLY_A_Z("Alphabetically A-Z"),
        ALPHABETICALLY_Z_A("Alphabetically Z-A"),
        PRICE_LOW_TO_HIGH("Price low to high"),
        PRICE_HIGH_TO_LOW("Price high to low"),
        DATE_OLD_TO_NEW("Date old to new"),
        DATE_NEW_TO_OLD("Date new to old");
        private final String displayName;
        SortBy(String displayName) {
            this.displayName = displayName;
        }
        public String getDisplayName() {
            return displayName;
        }
        @Override
        public String toString() {
            return displayName;
        }
    }
    private String search;
    private Integer categoryId;
    private SortBy sortBy;
    private Integer page = 0;
    private Integer pageSize = 10;
    private Integer priceFrom;
    private Integer priceTo;

    // Getters and Setters
}