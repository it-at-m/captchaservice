package de.muenchen.captchaservice.task;

import de.muenchen.captchaservice.service.expireddata.ExpiredDataService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeleteExpiredDataTask {
    private final ExpiredDataService expiredDataService;

    public DeleteExpiredDataTask(final ExpiredDataService expiredDataService) {
        this.expiredDataService = expiredDataService;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void deleteExpiredData() {
        try {
            expiredDataService.deleteExpiredData();
        } catch (Exception e) {
            log.error("Failed to delete expired data", e);
        }
    }
}
