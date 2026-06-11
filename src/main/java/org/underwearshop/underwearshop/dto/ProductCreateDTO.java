package org.underwearshop.underwearshop.dto;

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

    private String color;

    private String material;

    private String features;

    private BigDecimal price;

    private String bustModel;

    private String size;

    private String briefStyle;

    @NotNull
    private Long categoryId;
}