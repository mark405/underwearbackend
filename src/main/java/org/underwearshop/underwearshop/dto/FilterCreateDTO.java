package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterCreateDTO {

    @NotBlank
    private String name;
}