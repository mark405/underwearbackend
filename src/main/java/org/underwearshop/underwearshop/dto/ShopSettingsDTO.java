package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import org.underwearshop.underwearshop.entity.ShopSettings;

@Getter
public class ShopSettingsDTO {
    private final boolean pickupEnabled;

    public ShopSettingsDTO(ShopSettings shopSettings) {
        this.pickupEnabled = shopSettings.isPickupEnabled();
    }
}
