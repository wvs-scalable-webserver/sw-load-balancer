package de.wvs.sw.loadbalancer.tcp.pipeline.initialize;

import com.google.common.base.Preconditions;
import de.wvs.sw.loadbalancer.LoadBalancer;
import de.wvs.sw.loadbalancer.task.ConnectionsPerSecondTask;
import de.wvs.sw.loadbalancer.tcp.pipeline.handler.SocketUpstreamHandler;
import de.wvs.sw.loadbalancer.util.BackendInfo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class LoadBalancerSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Logger logger = LoggerFactory.getLogger(LoadBalancerSocketChannelInitializer.class);

    private int readTimeout;

    private int writeTimeout;

    private ConnectionsPerSecondTask connectionsPerSecondTask;

    public LoadBalancerSocketChannelInitializer(int readTimeout, int writeTimeout) {

        Preconditions.checkState(readTimeout > 0, "readTimeout cannot be negative");
        Preconditions.checkState(writeTimeout > 0, "writeTimeout cannot be negative");

        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
        this.connectionsPerSecondTask = LoadBalancer.getInstance().getConnectionsPerSecondTask();

        logger.debug("Read timeout: {}", readTimeout);
        logger.debug("Write timeout: {}", writeTimeout);
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        BackendInfo backendInfo = LoadBalancer.getBalancingStrategy()
                .selectBackend(channel.remoteAddress().getHostName(), channel.remoteAddress().getPort());

        if (backendInfo == null) {
            // Gracefully close the channel
            channel.close();

            logger.error("Unable to select a backend server. All down?");
            return;
        }

        channel.pipeline()
                .addLast(new ReadTimeoutHandler(readTimeout))
                .addLast(new WriteTimeoutHandler(writeTimeout));

        GlobalTrafficShapingHandler trafficShapingHandler = LoadBalancer.getInstance().getTrafficShapingHandler();
        if (trafficShapingHandler != null) {
            channel.pipeline().addLast(trafficShapingHandler);
        }

        channel.pipeline().addLast(new SocketUpstreamHandler(backendInfo));

        // Keep track of connections per second
        if (connectionsPerSecondTask != null) {
            connectionsPerSecondTask.inc();
        }

        logger.debug("Connected [{}] <-> [{}:{} ({})]", channel.remoteAddress(), backendInfo.getHost(), backendInfo.getPort(), backendInfo.getName());
    }
}
