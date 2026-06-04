package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterUpdateDTO {
    @NotBlank
    private String name;
}