package de.wvs.sw.loadbalancer.task;

import com.google.common.collect.Lists;
import de.wvs.sw.loadbalancer.LoadBalancer;
import de.wvs.sw.loadbalancer.strategy.BalancingStrategy;
import de.wvs.sw.loadbalancer.util.BackendInfo;

import java.util.List;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public abstract class CheckBackendTask implements Runnable {

    protected final List<BackendInfo> backendInfo;

    protected BalancingStrategy balancingStrategy;

    public CheckBackendTask(BalancingStrategy balancingStrategy) {

        this.balancingStrategy = balancingStrategy;
        this.backendInfo = Lists.newArrayList(balancingStrategy.getBackend());
    }

    public abstract void check();

    public synchronized void addBackend(BackendInfo backendInfo) {

        this.backendInfo.add(backendInfo);
    }

    public synchronized void removeBackend(BackendInfo backendInfo) {

        this.backendInfo.remove(backendInfo);
    }

    @Override
    public void run() {

        if (!LoadBalancer.getServerChannel().isActive()) {
            return;
        }

        check();
    }
}
