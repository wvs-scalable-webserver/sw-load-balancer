package de.wvs.sw.loadbalancer.strategy.impl;

import de.wvs.sw.loadbalancer.strategy.BalancingStrategy;
import de.wvs.sw.loadbalancer.util.BackendInfo;

import java.util.List;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class FastestBalancingStrategy extends BalancingStrategy {

    public FastestBalancingStrategy(List<BackendInfo> backend) {

        super(backend);
    }

    @Override
    public synchronized BackendInfo selectBackend(String originHost, int originPort) {

        BackendInfo current = null;
        double connectTime = Integer.MAX_VALUE;
        for (BackendInfo info : getBackend()) {
            if (info.getConnectTime() < connectTime) {
                connectTime = info.getConnectTime();
                current = info;
            }
        }

        return current;
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
