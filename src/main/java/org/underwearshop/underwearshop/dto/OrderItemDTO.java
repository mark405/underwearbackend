package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.underwearshop.underwearshop.entity.OrderItem;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public class OrderItemDTO {
    private final Long id;

    private final ProductDTO product;

    private final Long variantId;

    private final String size;

    private final String color;

    private final Integer quantity;

    private final BigDecimal price;

    public OrderItemDTO(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.product = new ProductDTO(orderItem.getProduct());
        this.variantId = orderItem.getProductVariant() != null ? orderItem.getProductVariant().getId() : null;
        this.size = orderItem.getSize();
        this.color = orderItem.getColor();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
    }
}
