package de.wvs.sw.loadbalancer.strategy.impl;

import de.wvs.sw.loadbalancer.strategy.BalancingStrategy;
import de.wvs.sw.loadbalancer.util.BackendInfo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class RoundRobinBalancingStrategy extends BalancingStrategy {

    private AtomicInteger currentTarget = new AtomicInteger(-1);

    public RoundRobinBalancingStrategy(List<BackendInfo> backend) {

        super(backend);
    }

    @Override
    public synchronized BackendInfo selectBackend(String originHost, int originPort) {

        int now = currentTarget.incrementAndGet();

        if (now == backend.size()) {
            now = 0;
            currentTarget.set(0);
        }

        return (!backend.isEmpty()) ? backend.get(now) : null;
    }

    @Override
    public void disconnectedFrom(BackendInfo backendInfo) {

    }

    @Override
    public void removeBackendStrategy(BackendInfo backendInfo) {

    }

    @Override
    public void addBackendStrategy(BackendInfo backendInfo) {

    }
}
