package org.underwearshop.underwearshop.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * One-time, idempotent data migration from the old single-SKU Product model (color/size/quantity
 * columns directly on `product`) to the new ProductVariant model. Hibernate's ddl-auto=update never
 * drops the legacy columns, so this reads them via plain SQL and backfills `product_variant` plus the
 * new `order_item.product_variant_id/size/color` columns. Safe to run on every startup: it only ever
 * inserts/updates rows that are still missing their migrated data.
 */
@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class ProductVariantMigration implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            migrate();
        } catch (Exception e) {
            log.error("Product variant migration failed, skipping", e);
        }
    }

    private void migrate() {
        if (!columnExists("product", "color")) {
            return;
        }

        List<Map<String, Object>> legacyProducts = jdbcTemplate.queryForList(
                "SELECT p.id, p.color, p.size, p.quantity, p.in_stock " +
                        "FROM product p " +
                        "WHERE NOT EXISTS (SELECT 1 FROM product_variant pv WHERE pv.product_id = p.id)"
        );

        int migrated = 0;
        for (Map<String, Object> row : legacyProducts) {
            String color = (String) row.get("color");
            String size = (String) row.get("size");

            if (color == null && size == null) {
                continue;
            }

            Long productId = ((Number) row.get("id")).longValue();
            int quantity = row.get("quantity") != null ? ((Number) row.get("quantity")).intValue() : 0;
            boolean inStock = row.get("in_stock") != null ? (Boolean) row.get("in_stock") : quantity > 0;

            jdbcTemplate.update(
                    "INSERT INTO product_variant (product_id, size, color, quantity, in_stock, active) " +
                            "VALUES (?, ?, ?, ?, ?, true)",
                    productId, size, color, quantity, inStock
            );
            migrated++;
        }

        if (migrated > 0) {
            log.info("Migrated {} legacy product(s) into product_variant", migrated);
        }

        int backfilled = jdbcTemplate.update(
                "UPDATE order_item oi " +
                        "SET product_variant_id = pv.id, size = pv.size, color = pv.color " +
                        "FROM product_variant pv " +
                        "WHERE oi.product_variant_id IS NULL " +
                        "AND pv.product_id = oi.product_id " +
                        "AND pv.id = (SELECT MIN(id) FROM product_variant WHERE product_id = oi.product_id)"
        );

        if (backfilled > 0) {
            log.info("Backfilled product_variant_id on {} legacy order_item row(s)", backfilled);
        }
    }

    private boolean columnExists(String table, String column) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = ? AND column_name = ?",
                Integer.class, table, column
        );
        return count != null && count > 0;
    }
}
