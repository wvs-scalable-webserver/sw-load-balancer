package de.wvs.sw.loadbalancer.strategy.impl;

import com.google.common.collect.Maps;
import de.wvs.sw.loadbalancer.strategy.BalancingStrategy;
import de.wvs.sw.loadbalancer.util.BackendInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class LeastConnectionBalancingStrategy extends BalancingStrategy {

    private Map<BackendInfo, Integer> connections = Maps.newConcurrentMap();

    public LeastConnectionBalancingStrategy(List<BackendInfo> backend) {

        super(backend);

        for (BackendInfo target : backend) {
            connections.put(target, 0);
        }
    }

    @Override
    public synchronized BackendInfo selectBackend(String originHost, int originPort) {

        int least = Integer.MAX_VALUE;
        BackendInfo leastBackend = null;
        for (Map.Entry<BackendInfo, Integer> entry : connections.entrySet()) {
            if (entry.getValue() < least) {
                least = entry.getValue();
                leastBackend = entry.getKey();
            }
        }

        connections.put(leastBackend, connections.get(leastBackend) + 1);

        return leastBackend;
    }

    @Override
    public void disconnectedFrom(BackendInfo backendInfo) {

        // Only update if the backend is still online
        Integer count = connections.get(backendInfo);
        if (count != null) {
            connections.put(backendInfo, count - 1);
        }
    }

    @Override
    public void removeBackendStrategy(BackendInfo backendInfo) {

        // Remove backend info if the server was removed from load balancing
        connections.remove(backendInfo);
    }

    @Override
    public void addBackendStrategy(BackendInfo backendInfo) {

        // Add backend info back if the server was added to the load balancing again
        connections.put(backendInfo, 0);
    }
}
