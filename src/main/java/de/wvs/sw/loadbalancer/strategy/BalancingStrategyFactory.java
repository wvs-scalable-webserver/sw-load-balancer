package de.wvs.sw.loadbalancer.strategy;

import de.wvs.sw.loadbalancer.strategy.impl.FastestBalancingStrategy;
import de.wvs.sw.loadbalancer.strategy.impl.LeastConnectionBalancingStrategy;
import de.wvs.sw.loadbalancer.strategy.impl.RandomBalancingStrategy;
import de.wvs.sw.loadbalancer.strategy.impl.RoundRobinBalancingStrategy;
import de.wvs.sw.loadbalancer.util.BackendInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public final class BalancingStrategyFactory {

    private static Logger logger = LoggerFactory.getLogger(BalancingStrategyFactory.class);

    private BalancingStrategyFactory() {
        // no instance
    }

    public static BalancingStrategy create(StrategyType type, List<BackendInfo> backendInfo) {

        if (type == null) {
            type = StrategyType.RANDOM;

            logger.info("Using default strategy: {}", type);
        } else {
            logger.info("Using strategy: {}", type);
        }

        switch (type) {
            case RANDOM:
                return new RandomBalancingStrategy(backendInfo);
            case ROUND_ROBIN:
                return new RoundRobinBalancingStrategy(backendInfo);
            case LEAST_CON:
                return new LeastConnectionBalancingStrategy(backendInfo);
            case FASTEST:
                return new FastestBalancingStrategy(backendInfo);
            default:
                throw new IllegalStateException("unknown strategy type '" + type + "'");
        }
    }
}
