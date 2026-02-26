package ai.causa.utils;

import ai.causa.svc.AllocatorService;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TimeBoundScheduler {
    private static final Logger LOG = Logger.getLogger(TimeBoundScheduler.class);

    @ConfigProperty(name = "crash.time.tick-millis", defaultValue = "500")
    long tickMillis;

    @PostConstruct
    void validateTickMillis() {
        if (tickMillis != 100 && tickMillis != 500 && tickMillis != 1000) {
            LOG.warnf(
                    "Unsupported crash.time.tick-millis value %d; auto-allocation on deadline will be disabled. " +
                            "Supported values are 100, 500, or 1000 milliseconds.",
                    tickMillis
            );
        }
    }

    @Inject
    AllocatorService svc;

    // Use a fixed-delay scheduler; the string must be constant, so pick among a few buckets.
    @Scheduled(every = "0.5s")
    void tickFast() {
        if (tickMillis == 500) svc.maybeAutoAllocateOnDeadline();
    }

    @Scheduled(every = "1s")
    void tick1s() {
        if (tickMillis == 1000) svc.maybeAutoAllocateOnDeadline();
    }

    @Scheduled(every = "0.1s")
    void tick100ms() {
        if (tickMillis == 100) svc.maybeAutoAllocateOnDeadline();
    }
}
