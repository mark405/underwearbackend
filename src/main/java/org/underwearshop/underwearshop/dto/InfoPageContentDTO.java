package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import org.underwearshop.underwearshop.entity.InfoPageContent;

@Getter
public class InfoPageContentDTO {
    private final String content;

    public InfoPageContentDTO(InfoPageContent infoPageContent) {
        this.content = infoPageContent.getContent();
    }
}
