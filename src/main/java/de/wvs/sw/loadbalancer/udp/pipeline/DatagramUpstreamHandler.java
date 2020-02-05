package de.wvs.sw.loadbalancer.udp.pipeline;

import de.wvs.sw.loadbalancer.LoadBalancer;
import de.wvs.sw.loadbalancer.task.ConnectionsPerSecondTask;
import de.wvs.sw.loadbalancer.udp.LoadBalancerDatagram;
import de.wvs.sw.loadbalancer.util.BackendInfo;
import de.wvs.sw.loadbalancer.util.ChannelUtil;
import de.wvs.sw.loadbalancer.util.PipelineUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class DatagramUpstreamHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static Logger logger = LoggerFactory.getLogger(DatagramUpstreamHandler.class);

    private ConnectionsPerSecondTask connectionsPerSecondTask;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        connectionsPerSecondTask = LoadBalancer.getInstance().getConnectionsPerSecondTask();

        // Add the traffic counter
        ctx.channel().pipeline().addLast(LoadBalancer.getInstance().getTrafficShapingHandler());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {

        BackendInfo backendInfo = LoadBalancerDatagram.getBalancingStrategy().selectBackend("", 0);

        if (backendInfo == null) {
            logger.error("Unable to select a backend server. All down?");
            return;
        }

        // Only copy if there is at least one backend server
        ByteBuf copy = datagramPacket.content().copy().retain();

        Bootstrap bootstrap = new Bootstrap()
                .channel(PipelineUtils.getDatagramChannel())
                .handler(new DatagramDownstreamHandler(ctx.channel(), datagramPacket.sender()))
                .group(ctx.channel().eventLoop());

        ChannelFuture channelFuture = bootstrap.bind(0);

        // Add the traffic shaping handler to the channel pipeline
        GlobalTrafficShapingHandler trafficShapingHandler = LoadBalancer.getInstance().getTrafficShapingHandler();
        if (trafficShapingHandler != null) {
            // The handler needs to be the first handler in the pipeline
            channelFuture.channel().pipeline().addFirst(trafficShapingHandler);
        }

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {

                Channel channel = channelFuture.channel();
                if (channelFuture.isSuccess()) {
                    channel.writeAndFlush(new DatagramPacket(copy, new InetSocketAddress(backendInfo.getHost(), backendInfo.getPort())));
                    // Release the buffer
                    copy.release();
                } else {
                    ChannelUtil.close(channel);
                }
            }
        });

        // Keep track of request per second
        if (connectionsPerSecondTask != null) {
            connectionsPerSecondTask.inc();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ChannelUtil.close(ctx.channel());

        if (!(cause instanceof IOException)) {
            logger.error(cause.getMessage(), cause);
        }
    }
}
