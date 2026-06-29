package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.underwearshop.underwearshop.entity.Product;
import org.underwearshop.underwearshop.entity.ProductImage;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ProductDTO {
    private final Long id;
    private final String name;
    private final String image;
    private final List<String> images;
    private final String circumference;
    private final String cup;
    private final String color;
    private final String material;
    private final String features;
    private final BigDecimal price;
    private final Integer quantity;
    private final Boolean inStock;
    private final String size;
    private final CategoryDTO category;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.image = product.getImage();
        this.images = product.getImages() != null ? product.getImages().stream().map(ProductImage::getImage).toList() : null;
        this.circumference = product.getCircumference();
        this.cup = product.getCup();
        this.color = product.getColor();
        this.material = product.getMaterial();
        this.features = product.getFeatures();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.inStock = product.getInStock();
        this.size = product.getSize();

        this.category = new CategoryDTO(product.getCategory());
    }
}
