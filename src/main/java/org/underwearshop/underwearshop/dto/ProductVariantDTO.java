package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.underwearshop.underwearshop.entity.ProductVariant;

@RequiredArgsConstructor
@Getter
public class ProductVariantDTO {
    private final Long id;
    private final String size;
    private final String color;
    private final Integer quantity;
    private final Boolean inStock;

    public ProductVariantDTO(ProductVariant variant) {
        this.id = variant.getId();
        this.size = variant.getSize();
        this.color = variant.getColor();
        this.quantity = variant.getQuantity();
        this.inStock = variant.getInStock();
    }
}
