package com.ecom.dtos.requests;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class ProductQuery {
    @Getter
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

        @Override
        public String toString() {
            return displayName;
        }
    }

    private String search;
    private List<Integer> categoryId;
    private Boolean isInStock =null;
    private SortBy sortBy;
    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer priceFrom;
    private Integer priceTo;

    // Getters and Setters
}