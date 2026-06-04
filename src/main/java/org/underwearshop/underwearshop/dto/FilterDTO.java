package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import org.underwearshop.underwearshop.entity.Filter;

@Getter
public class FilterDTO {
    private final Long id;
    private final String name;
    private final CategoryDTO category;

    public FilterDTO(Filter filter) {
        this.id = filter.getId();
        this.name = filter.getName();
        this.category = new CategoryDTO(filter.getCategory());
    }
}
