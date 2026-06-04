package org.underwearshop.underwearshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.Category;
import org.underwearshop.underwearshop.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
