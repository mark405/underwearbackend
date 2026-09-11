package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.underwearshop.underwearshop.entity.AttributeOption;
import org.underwearshop.underwearshop.entity.AttributeType;

@RequiredArgsConstructor
@Getter
public class AttributeOptionDTO {
    private final Long id;
    private final AttributeType type;
    private final String value;
    private final String label;

    public AttributeOptionDTO(AttributeOption option) {
        this.id = option.getId();
        this.type = option.getType();
        this.value = option.getValue();
        this.label = option.getLabel();
    }
}
