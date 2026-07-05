package org.underwearshop.underwearshop.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.underwearshop.underwearshop.dto.ProductFilter;
import org.underwearshop.underwearshop.entity.Product;
import org.underwearshop.underwearshop.entity.ProductVariant;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> filter(ProductFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.or(
                    cb.isNull(root.get("deleted")),
                    cb.isFalse(root.get("deleted"))
            ));

            if (filter.name() != null && !filter.name().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + filter.name().toLowerCase() + "%"
                        )
                );
            }

            if (filter.circumference() != null) {
                predicates.add(cb.equal(root.get("circumference"), filter.circumference()));
            }

            if (filter.cup() != null) {
                predicates.add(cb.equal(root.get("cup"), filter.cup()));
            }

            if (filter.color() != null) {
                predicates.add(variantExists(root, query, cb, v -> cb.equal(v.get("color"), filter.color())));
            }

            if (filter.material() != null) {
                predicates.add(cb.equal(root.get("material"), filter.material()));
            }

            if (filter.features() != null && !filter.features().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("features")),
                                "%" + filter.features().toLowerCase() + "%"
                        )
                );
            }

            if (filter.minPrice() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice())
                );
            }

            if (filter.maxPrice() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice())
                );
            }

            if (filter.inStock() != null) {
                Predicate exists = variantExists(root, query, cb, v -> cb.isTrue(v.get("inStock")));
                predicates.add(filter.inStock() ? exists : cb.not(exists));
            }

            if (filter.bustModel() != null) {
                predicates.add(cb.equal(root.get("bustModel"), filter.bustModel()));
            }

            if (filter.size() != null) {
                predicates.add(variantExists(root, query, cb, v -> cb.equal(v.get("size"), filter.size())));
            }

            if (filter.briefStyle() != null) {
                predicates.add(cb.equal(root.get("briefStyle"), filter.briefStyle()));
            }

            if (filter.categoryId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("category").get("id"),
                                filter.categoryId()
                        )
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    /**
     * Builds an EXISTS(SELECT 1 FROM ProductVariant v WHERE v.product = root AND v.active = true AND <extra>)
     * predicate, i.e. "the product has at least one active variant matching the extra condition".
     */
    private static Predicate variantExists(
            Root<Product> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            Function<Root<ProductVariant>, Predicate> extra
    ) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<ProductVariant> variant = subquery.from(ProductVariant.class);
        subquery.select(variant.get("id"));
        subquery.where(
                cb.and(
                        cb.equal(variant.get("product"), root),
                        cb.isTrue(variant.get("active")),
                        extra.apply(variant)
                )
        );
        return cb.exists(subquery);
    }
}
