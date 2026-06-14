package org.underwearshop.underwearshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNullOrderByIdAsc();

    List<Category> findByParentIdOrderByIdAsc(Long parentId);
}
