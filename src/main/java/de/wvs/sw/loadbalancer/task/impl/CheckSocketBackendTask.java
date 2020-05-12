package de.wvs.sw.loadbalancer.task.impl;

import de.wvs.sw.loadbalancer.strategy.BalancingStrategy;
import de.wvs.sw.loadbalancer.task.CheckBackendTask;
import de.wvs.sw.loadbalancer.util.BackendInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class CheckSocketBackendTask extends CheckBackendTask {

    private static Logger logger = LoggerFactory.getLogger(CheckSocketBackendTask.class);

    public CheckSocketBackendTask(BalancingStrategy balancingStrategy) {

        super(balancingStrategy);
    }

    @Override
    public void check() {

        for (BackendInfo info : backendInfo) {
            if (info.checkSocket()) {
                if (!balancingStrategy.hasBackend(info)) {
                    balancingStrategy.addBackend(info);
                    logger.info("{} is up again and was added back to the load balancer", info.getName());
                }
            } else {
                if (balancingStrategy.hasBackend(info)) {
                    logger.warn("{} went down and was removed from the load balancer", info.getName());
                    balancingStrategy.removeBackend(info);

                    if (balancingStrategy.getBackend().size() == 0) {
                        logger.error("No more backend servers online");
                    } else {
                        logger.info("{} backend servers left", balancingStrategy.getBackend().size());
                    }
                }
            }
        }
    }
}
