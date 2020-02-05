package de.wvs.sw.loadbalancer.udp.pipeline;

import de.wvs.sw.loadbalancer.util.ChannelUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class DatagramDownstreamHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Logger logger = LoggerFactory.getLogger(DatagramDownstreamHandler.class);

    private Channel channel;

    private InetSocketAddress sender;

    public DatagramDownstreamHandler(Channel channel, InetSocketAddress sender) {

        this.channel = channel;
        this.sender = sender;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {

        if (channel.isActive()) {
            channel.writeAndFlush(new DatagramPacket(datagramPacket.content().retain(), sender));
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
