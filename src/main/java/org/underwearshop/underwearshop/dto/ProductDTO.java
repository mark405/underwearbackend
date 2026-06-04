package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.underwearshop.underwearshop.entity.Product;

@RequiredArgsConstructor
@Getter
public class ProductDTO {
    private final Long id;
    private final String name;
    private final String image;
    private final CategoryDTO category;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.image = product.getImage();
        this.category = new CategoryDTO(product.getCategory());
    }
}
