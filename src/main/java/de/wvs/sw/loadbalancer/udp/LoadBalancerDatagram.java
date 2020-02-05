package de.wvs.sw.loadbalancer.udp;

import de.wvs.sw.loadbalancer.LoadBalancer;
import de.wvs.sw.loadbalancer.udp.pipeline.DatagramUpstreamHandler;
import de.wvs.sw.loadbalancer.util.PipelineUtils;
import de.progme.iris.IrisConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class LoadBalancerDatagram extends LoadBalancer {

    private static Logger logger = LoggerFactory.getLogger(LoadBalancerDatagram.class);

    public LoadBalancerDatagram(IrisConfig irisConfig) {

        super(irisConfig);
    }

    @Override
    public Channel bootstrap(EventLoopGroup bossGroup, EventLoopGroup workerGroup, String ip, int port, int backlog, int readTimeout, int writeTimeout) throws Exception {

        logger.info("Bootstrapping datagram server");

        Bootstrap bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(PipelineUtils.getDatagramChannel())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new DatagramUpstreamHandler());

        if (PipelineUtils.isEpoll()) {
            bootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.LEVEL_TRIGGERED);

            logger.debug("Epoll mode is now level triggered");
        }

        return bootstrap
                .bind(ip, port)
                .sync()
                .channel();
    }
}
