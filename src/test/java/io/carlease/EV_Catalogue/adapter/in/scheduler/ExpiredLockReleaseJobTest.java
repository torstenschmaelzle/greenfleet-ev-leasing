package io.carlease.EV_Catalogue.adapter.in.scheduler;

import io.carlease.EV_Catalogue.application.port.in.ReleaseExpiredLocksUseCase;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ExpiredLockReleaseJobTest {

    private final ReleaseExpiredLocksUseCase useCase = mock(ReleaseExpiredLocksUseCase.class);
    private final ExpiredLockReleaseJob job = new ExpiredLockReleaseJob(useCase);

    @Test
    void delegatesToUseCase() {
        job.releaseExpiredLocks();

        then(useCase).should().releaseExpiredLocks();
    }
}
