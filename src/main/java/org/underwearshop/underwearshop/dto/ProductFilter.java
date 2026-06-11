package org.underwearshop.underwearshop.dto;

import java.math.BigDecimal;

public record ProductFilter(
        String name,
        String circumference,
        String cup,
        String color,
        String material,
        String features,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean inStock,
        String bustModel,
        String size,
        String briefStyle,
        Long categoryId
) {
}