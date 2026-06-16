package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.Setter;
import org.underwearshop.underwearshop.entity.OrderStatus;

@Getter
@Setter
public class OrderUpdateDTO {

    private String username;

    private String telephone;

    private String deliveryType;

    private String deliveryAddress;

    private OrderStatus status;
}