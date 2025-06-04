package de.muenchen.captchaservice.task;

import de.muenchen.captchaservice.service.invalidateddate.InvalidatedDataService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DeleteInvalidatedDataTask {
    private final InvalidatedDataService invalidatedDataService;

    @SuppressFBWarnings(value = { "EI_EXPOSE_REP2" }, justification = "Dependency Injection")
    public DeleteInvalidatedDataTask(final InvalidatedDataService invalidatedDataService) {
        this.invalidatedDataService = invalidatedDataService;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void deleteInvalidatedData() {
        invalidatedDataService.deleteInvalidatedData();
    }
}
