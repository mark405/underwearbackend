package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.underwearshop.underwearshop.entity.Product;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public class ProductShortDTO {
    private final Long id;
    private final String name;
    private final String image;
    private final BigDecimal price;
    private final Boolean inStock;
    private final CategoryDTO category;

    public ProductShortDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.image = product.getImage();
        this.price = product.getPrice();
        this.inStock = product.getInStock();

        this.category = new CategoryDTO(product.getCategory());
    }
}
