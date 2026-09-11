package org.underwearshop.underwearshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.PersistenceCreator;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__(@PersistenceCreator))
@Entity
public class AttributeOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AttributeType type;

    /**
     * The raw value stored on Product/ProductVariant (color/material/features/circumference/cup/size).
     * Product and ProductVariant never reference this entity by id - they just copy this string - so
     * deleting an AttributeOption never breaks existing products, it only removes the option from
     * future dropdowns.
     */
    private String value;

    /**
     * Display label. For seeded built-in options this differs from `value` (e.g. value="black",
     * label="Чорний"); for admin-created custom options label == value.
     */
    private String label;

    /**
     * Preserves the curated order of the original hardcoded lists. Null for custom options added later,
     * which are sorted after all seeded ones (by id).
     */
    private Integer sortOrder;
}
