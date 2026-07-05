package org.underwearshop.underwearshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.underwearshop.underwearshop.dto.ShopSettingsDTO;
import org.underwearshop.underwearshop.dto.ShopSettingsUpdateDTO;
import org.underwearshop.underwearshop.service.ShopSettingsService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shop-settings")
public class ShopSettingsController {
    private final ShopSettingsService shopSettingsService;

    @GetMapping
    public ShopSettingsDTO get() {
        return new ShopSettingsDTO(shopSettingsService.get());
    }

    @PutMapping("/admin")
    public ShopSettingsDTO update(@RequestBody @Valid ShopSettingsUpdateDTO dto) {
        return new ShopSettingsDTO(shopSettingsService.update(dto.isPickupEnabled()));
    }
}
