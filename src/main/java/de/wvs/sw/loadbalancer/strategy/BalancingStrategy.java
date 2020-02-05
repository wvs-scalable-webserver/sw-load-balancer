package de.wvs.sw.loadbalancer.strategy;

import de.wvs.sw.loadbalancer.util.BackendInfo;

import java.util.Collections;
import java.util.List;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public abstract class BalancingStrategy {

    protected List<BackendInfo> backend;

    public BalancingStrategy(List<BackendInfo> backend) {

        this.backend = Collections.synchronizedList(backend);
    }

    public abstract BackendInfo selectBackend(String originHost, int originPort);

    public abstract void disconnectedFrom(BackendInfo backendInfo);

    public abstract void removeBackendStrategy(BackendInfo backendInfo);

    public abstract void addBackendStrategy(BackendInfo backendInfo);

    public synchronized void addBackend(BackendInfo targetData) {

        backend.add(targetData);

        addBackendStrategy(targetData);
    }

    public synchronized void removeBackend(BackendInfo targetData) {

        backend.remove(targetData);

        removeBackendStrategy(targetData);
    }

    public boolean hasBackend(BackendInfo backendInfo) {

        return backend.contains(backendInfo);
    }

    public List<BackendInfo> getBackend() {

        return backend;
    }
}
