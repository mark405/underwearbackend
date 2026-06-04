package org.underwearshop.underwearshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.Category;
import org.underwearshop.underwearshop.entity.Filter;

@Repository
public interface FilterRepository extends JpaRepository<Filter, Long> {
}
