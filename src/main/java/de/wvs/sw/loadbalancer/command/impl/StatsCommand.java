package de.wvs.sw.loadbalancer.command.impl;

import de.wvs.sw.loadbalancer.LoadBalancer;
import de.wvs.sw.loadbalancer.command.Command;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class StatsCommand extends Command {

    private static Logger logger = LoggerFactory.getLogger(StatsCommand.class);

    public StatsCommand(String name, String description, String... aliases) {

        super(name, description, aliases);
    }

    @Override
    public boolean execute(String[] args) {

        logger.info("Connections: {}", LoadBalancer.getChannelGroup().size());
        if (LoadBalancer.getInstance().getConnectionsPerSecondTask() != null) {
            logger.info("Connections per second: {}", LoadBalancer.getInstance().getConnectionsPerSecondTask().getPerSecond());
        }
        logger.info("Online backend servers: {}", LoadBalancer.getBalancingStrategy().getBackend().size());

        GlobalTrafficShapingHandler trafficShapingHandler = LoadBalancer.getInstance().getTrafficShapingHandler();
        if (trafficShapingHandler != null) {
            TrafficCounter trafficCounter = trafficShapingHandler.trafficCounter();

            logger.info("Current bytes read: {}", trafficCounter.currentReadBytes());
            logger.info("Current bytes written: {}", trafficCounter.currentWrittenBytes());
            logger.info("Last read throughput: {}", trafficCounter.lastReadThroughput());
            logger.info("Last write throughput: {}", trafficCounter.lastWrittenBytes());
            logger.info("Total bytes read: {}", trafficCounter.cumulativeReadBytes());
            logger.info("Total bytes written: {}", trafficCounter.cumulativeWrittenBytes());
        }

        return true;
    }
}
