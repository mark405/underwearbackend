package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String telephone;
    @NotBlank
    private String deliveryType;

    private String deliveryAddress;

    private List<OrderItemCreateDTO> orderItems;
}