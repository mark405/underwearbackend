package org.underwearshop.underwearshop.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.Category;
import org.underwearshop.underwearshop.entity.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @EntityGraph(attributePaths = {"images",})
    Optional<Product> findById(Long id);

    boolean existsByCategoryId(Long categoryId);
}
