package io.carlease.EV_Catalogue.adapter.in.scheduler;

import io.carlease.EV_Catalogue.application.port.in.ReleaseExpiredLocksUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpiredLockReleaseJob {

    private final ReleaseExpiredLocksUseCase releaseExpiredLocksUseCase;

    public ExpiredLockReleaseJob(ReleaseExpiredLocksUseCase releaseExpiredLocksUseCase) {
        this.releaseExpiredLocksUseCase = releaseExpiredLocksUseCase;
    }

    @Scheduled(fixedRate = 60_000)
    public void releaseExpiredLocks() {
        releaseExpiredLocksUseCase.releaseExpiredLocks();
    }
}
