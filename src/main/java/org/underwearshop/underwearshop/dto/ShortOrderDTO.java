package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderStatus;

@RequiredArgsConstructor
@Getter
public class ShortOrderDTO {
    private final Long id;

    private final String username;

    private final String telephone;

    private final String deliveryType;

    private final String deliveryAddress;

    private final OrderStatus status;

    public ShortOrderDTO(Order order) {
        this.id = order.getId();
        this.username = order.getUsername();
        this.telephone = order.getTelephone();
        this.deliveryType = order.getDeliveryType();
        this.deliveryAddress = order.getDeliveryAddress();
        this.status = order.getStatus();
    }
}
