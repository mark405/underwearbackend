package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreateDTO {
    @NotBlank
    private String name;

    private String circumference;

    private String cup;

    @NotBlank
    private String color;

    @NotBlank
    private String material;

    @NotBlank
    private String features;

    @NotNull
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer quantity;

    @NotBlank
    private String size;

    @NotNull
    private Long categoryId;
}