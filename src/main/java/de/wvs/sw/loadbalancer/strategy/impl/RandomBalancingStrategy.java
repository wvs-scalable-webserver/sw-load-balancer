package de.wvs.sw.loadbalancer.strategy.impl;

import de.wvs.sw.loadbalancer.strategy.BalancingStrategy;
import de.wvs.sw.loadbalancer.util.BackendInfo;

import java.util.List;
import java.util.Random;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class RandomBalancingStrategy extends BalancingStrategy {

    private Random random = new Random();

    public RandomBalancingStrategy(List<BackendInfo> backend) {

        super(backend);
    }

    @Override
    public synchronized BackendInfo selectBackend(String originHost, int originPort) {

        return (!backend.isEmpty()) ? backend.get(random.nextInt(backend.size())) : null;
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
