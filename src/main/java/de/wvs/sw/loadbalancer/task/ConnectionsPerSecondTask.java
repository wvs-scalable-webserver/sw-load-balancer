package de.wvs.sw.loadbalancer.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class ConnectionsPerSecondTask {

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private AtomicInteger value = new AtomicInteger();

    private int lastValue = 0;

    private int perSecond = 0;

    public ConnectionsPerSecondTask() {

        // TODO: Use shared executor service
        scheduledExecutorService.scheduleAtFixedRate(this::check, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stop() {

        scheduledExecutorService.shutdown();
    }

    public void inc() {

        value.incrementAndGet();
    }

    private void check() {

        int now = value.get();

        perSecond = Math.max(0, now - lastValue);

        lastValue = now;
    }

    public int getPerSecond() {

        return perSecond;
    }
}
