package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemCreateDTO {
    @NotNull
    private Long productId;
    @NotNull
    private Integer quantity;
}