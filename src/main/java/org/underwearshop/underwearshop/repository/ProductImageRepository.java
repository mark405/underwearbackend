package org.underwearshop.underwearshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.Product;
import org.underwearshop.underwearshop.entity.ProductImage;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    void deleteByProductIdAndImageIn(Long productId, Collection<String> images);
}
