package org.underwearshop.underwearshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underwearshop.underwearshop.entity.ShopSettings;
import org.underwearshop.underwearshop.repository.ShopSettingsRepository;

@Service
@RequiredArgsConstructor
public class ShopSettingsService {
    public static final long SETTINGS_ID = 1L;

    private final ShopSettingsRepository shopSettingsRepository;

    @Transactional(readOnly = true)
    public ShopSettings get() {
        return shopSettingsRepository.findById(SETTINGS_ID)
                .orElseGet(() -> shopSettingsRepository.save(
                        ShopSettings.builder()
                                .id(SETTINGS_ID)
                                .pickupEnabled(true)
                                .build()
                ));
    }

    @Transactional
    public ShopSettings update(boolean pickupEnabled) {
        ShopSettings shopSettings = get();
        shopSettings.setPickupEnabled(pickupEnabled);

        return shopSettingsRepository.save(shopSettings);
    }
}
