package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import org.underwearshop.underwearshop.entity.Banner;

@Getter
public class BannerDTO {
    private final Long id;
    private final String image;
    private final Integer position;

    public BannerDTO(Banner banner) {
        this.id = banner.getId();
        this.image = banner.getImage();
        this.position = banner.getPosition();
    }
}
