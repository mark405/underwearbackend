package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.underwearshop.underwearshop.entity.Product;
import org.underwearshop.underwearshop.entity.ProductVariant;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ProductShortDTO {
    private final Long id;
    private final String name;
    private final String image;
    private final BigDecimal price;
    private final Integer quantity;
    private final Boolean inStock;
    private final CategoryDTO category;

    public ProductShortDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.image = product.getImage();
        this.price = product.getPrice();

        List<ProductVariant> activeVariants = product.getVariants() != null
                ? product.getVariants().stream().filter(v -> Boolean.TRUE.equals(v.getActive())).toList()
                : List.of();

        this.quantity = activeVariants.stream().mapToInt(v -> v.getQuantity() != null ? v.getQuantity() : 0).sum();
        this.inStock = activeVariants.stream().anyMatch(v -> Boolean.TRUE.equals(v.getInStock()));

        this.category = new CategoryDTO(product.getCategory());
    }
}
