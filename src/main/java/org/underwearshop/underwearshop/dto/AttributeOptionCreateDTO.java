package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.underwearshop.underwearshop.entity.AttributeType;

@Getter
@Setter
public class AttributeOptionCreateDTO {
    @NotNull
    private AttributeType type;

    @NotBlank
    private String value;
}
