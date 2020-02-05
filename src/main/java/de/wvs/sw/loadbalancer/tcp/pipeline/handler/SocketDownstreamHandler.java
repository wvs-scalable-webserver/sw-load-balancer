package de.wvs.sw.loadbalancer.tcp.pipeline.handler;

import de.wvs.sw.loadbalancer.util.ChannelUtil;
import io.netty.channel.*;
import io.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class SocketDownstreamHandler extends ChannelHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(SocketDownstreamHandler.class);

    private Channel inboundChannel;

    public SocketDownstreamHandler(Channel inboundChannel) {

        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {

        if (inboundChannel.isActive()) {
            inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {

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

        ChannelUtil.closeOnFlush(inboundChannel);
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
