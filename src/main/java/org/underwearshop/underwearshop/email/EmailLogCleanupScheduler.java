package org.underwearshop.underwearshop.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.underwearshop.underwearshop.repository.EmailLogRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailLogCleanupScheduler {

    private final EmailLogRepository emailLogRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupOldEmailLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusMonths(1);

        emailLogRepository.deleteAllByCreatedAtBefore(cutoff);
        log.info("Cleaned up email logs created before {}", cutoff);
    }
}
