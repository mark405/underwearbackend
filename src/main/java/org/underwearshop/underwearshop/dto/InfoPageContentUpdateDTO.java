package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoPageContentUpdateDTO {
    @NotBlank
    private String content;
}
