package org.underwearshop.underwearshop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryReorderDTO {

    @NotEmpty
    private List<Long> orderedIds;
}
