package de.wvs.sw.loadbalancer.tcp;

import de.wvs.sw.loadbalancer.LoadBalancer;
import de.wvs.sw.loadbalancer.tcp.pipeline.initialize.LoadBalancerSocketChannelInitializer;
import de.wvs.sw.loadbalancer.util.PipelineUtils;
import de.progme.iris.IrisConfig;
import io.netty.bootstrap.ServerBootstrap;
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
public class LoadBalancerSocket extends LoadBalancer {

    private static Logger logger = LoggerFactory.getLogger(LoadBalancerSocket.class);

    public LoadBalancerSocket(IrisConfig irisConfig) {

        super(irisConfig);
    }

    @Override
    public Channel bootstrap(EventLoopGroup bossGroup, EventLoopGroup workerGroup, String ip, int port, int backlog, int readTimeout, int writeTimeout) throws Exception {

        logger.info("Bootstrapping socket server");

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(PipelineUtils.getServerChannel())
                .childHandler(new LoadBalancerSocketChannelInitializer(readTimeout, writeTimeout))
                .childOption(ChannelOption.AUTO_READ, false);

        if (PipelineUtils.isEpoll()) {
            bootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.LEVEL_TRIGGERED);

            logger.debug("Epoll mode is now level triggered");
        }

        return bootstrap
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, backlog)
                .bind(ip, port)
                .sync()
                .channel();
    }
}
