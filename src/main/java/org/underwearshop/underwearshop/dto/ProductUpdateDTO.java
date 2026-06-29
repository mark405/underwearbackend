package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductUpdateDTO {
    @NotBlank
    private String name;

    private String circumference;

    private String cup;

    private String color;

    private String material;

    private String features;

    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer quantity;

    private String size;

    private List<String> imagesToDelete = new ArrayList<>();

}