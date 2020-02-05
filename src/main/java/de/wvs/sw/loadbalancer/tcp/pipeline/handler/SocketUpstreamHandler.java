package de.wvs.sw.loadbalancer.tcp.pipeline.handler;

import de.wvs.sw.loadbalancer.LoadBalancer;
import de.wvs.sw.loadbalancer.tcp.LoadBalancerSocket;
import de.wvs.sw.loadbalancer.util.BackendInfo;
import de.wvs.sw.loadbalancer.util.ChannelUtil;
import de.wvs.sw.loadbalancer.util.PipelineUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class SocketUpstreamHandler extends ChannelHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(SocketUpstreamHandler.class);

    private BackendInfo backendInfo;

    private Channel downstreamChannel;

    public SocketUpstreamHandler(BackendInfo backendInfo) {

        this.backendInfo = backendInfo;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        final Channel inboundChannel = ctx.channel();

        Bootstrap b = new Bootstrap()
                .group(inboundChannel.eventLoop())
                .channel(PipelineUtils.getChannel())
                .handler(new SocketDownstreamHandler(inboundChannel))
                .option(ChannelOption.TCP_NODELAY, true)
                // No initial connection should take longer than 4 seconds
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, BackendInfo.DEFAULT_TCP_TIMEOUT)
                .option(ChannelOption.AUTO_READ, false);

        ChannelFuture f = b.connect(backendInfo.getHost(), backendInfo.getPort());
        downstreamChannel = f.channel();
        f.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) {

                if (future.isSuccess()) {
                    inboundChannel.read();
                } else {
                    inboundChannel.close();
                }
            }
        });

        // Add the channel to the channel group
        LoadBalancer.getChannelGroup().add(inboundChannel);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {

        if (downstreamChannel.isActive()) {
            downstreamChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) {

                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        ChannelUtil.closeOnFlush(downstreamChannel);

        LoadBalancerSocket.getBalancingStrategy().disconnectedFrom(backendInfo);

        logger.debug("Disconnected [{}] <-> [{}:{} ({})]", ctx.channel().remoteAddress(), backendInfo.getHost(), backendInfo.getPort(), backendInfo.getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        ChannelUtil.closeOnFlush(ctx.channel());

        // Ignore IO and timeout related exceptions
        if (!(cause instanceof IOException) && !(cause instanceof TimeoutException)) {
            logger.error(cause.getMessage(), cause);
        }
    }
}
