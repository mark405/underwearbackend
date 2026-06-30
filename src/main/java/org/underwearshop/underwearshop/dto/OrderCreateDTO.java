package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    private List<OrderItemCreateDTO> orderItems;
}