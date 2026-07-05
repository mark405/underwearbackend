package org.underwearshop.underwearshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductCreateDTO {
    @NotBlank
    private String name;

    private String circumference;

    private String cup;

    @NotBlank
    private String material;

    @NotBlank
    private String features;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Long categoryId;

    @NotEmpty
    @Valid
    private List<ProductVariantRequestDTO> variants;
}
