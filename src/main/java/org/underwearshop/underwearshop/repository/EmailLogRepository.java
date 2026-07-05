package org.underwearshop.underwearshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.EmailLog;

import java.time.LocalDateTime;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    void deleteAllByCreatedAtBefore(LocalDateTime cutoff);
}
