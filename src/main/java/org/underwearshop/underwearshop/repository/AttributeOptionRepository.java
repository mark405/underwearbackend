package org.underwearshop.underwearshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.AttributeOption;
import org.underwearshop.underwearshop.entity.AttributeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeOptionRepository extends JpaRepository<AttributeOption, Long> {
    List<AttributeOption> findAllByTypeOrderBySortOrderAscIdAsc(AttributeType type);

    Optional<AttributeOption> findByTypeAndValueIgnoreCase(AttributeType type, String value);

    boolean existsByType(AttributeType type);
}
