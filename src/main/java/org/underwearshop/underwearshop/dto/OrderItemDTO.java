package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderItem;
import org.underwearshop.underwearshop.entity.OrderStatus;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public class OrderItemDTO {
    private final Long id;

    private final ProductDTO product;

    private final Integer quantity;

    private final BigDecimal price;

    public OrderItemDTO(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.product = new ProductDTO(orderItem.getProduct());
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
    }
}
