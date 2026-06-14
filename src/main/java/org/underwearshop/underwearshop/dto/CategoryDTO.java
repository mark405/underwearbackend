package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import org.underwearshop.underwearshop.entity.Category;

@Getter
public class CategoryDTO {
    private final Long id;
    private final String name;
    private final String image;
    private final CategoryDTO parent;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.image = category.getImage();
        this.parent = category.getParent() != null ? new CategoryDTO(category.getParent()) : null;
    }
}
