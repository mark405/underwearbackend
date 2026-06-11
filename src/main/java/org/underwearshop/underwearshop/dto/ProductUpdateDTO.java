package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.NotBlank;
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

    private Boolean inStock;

    private String bustModel;

    private String size;

    private String briefStyle;

    private List<String> imagesToDelete = new ArrayList<>();

}