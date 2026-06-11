package org.underwearshop.underwearshop.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.underwearshop.underwearshop.dto.ProductFilter;
import org.underwearshop.underwearshop.entity.Product;

import java.util.ArrayList;
import java.util.List;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> filter(ProductFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

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
                predicates.add(cb.equal(root.get("color"), filter.color()));
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
                predicates.add(cb.equal(root.get("inStock"), filter.inStock()));
            }

            if (filter.bustModel() != null) {
                predicates.add(cb.equal(root.get("bustModel"), filter.bustModel()));
            }

            if (filter.size() != null) {
                predicates.add(cb.equal(root.get("size"), filter.size()));
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
}