package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductVariantRequestDTO {
    /**
     * Null for a new variant. When present, must reference an existing variant of the
     * product being updated (matched/updated in place instead of creating a duplicate).
     */
    private Long id;

    @NotBlank
    private String size;

    @NotBlank
    private String color;

    @NotNull
    @Min(0)
    private Integer quantity;
}
