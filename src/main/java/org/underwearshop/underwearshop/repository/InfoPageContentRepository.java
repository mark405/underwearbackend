package org.underwearshop.underwearshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.InfoPageContent;

@Repository
public interface InfoPageContentRepository extends JpaRepository<InfoPageContent, Long> {
}
