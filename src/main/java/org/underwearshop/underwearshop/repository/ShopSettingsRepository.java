package org.underwearshop.underwearshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.ShopSettings;

@Repository
public interface ShopSettingsRepository extends JpaRepository<ShopSettings, Long> {
}
