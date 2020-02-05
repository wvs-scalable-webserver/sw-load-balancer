package de.wvs.sw.loadbalancer.util;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;


public final class ChannelUtil {

    private ChannelUtil() {
/**
 * Created by Marvin Erkes on 04.02.2020.
 */        // no instance
    }

    public static void closeOnFlush(Channel ch) {

        if (ch != null && ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static void close(Channel ch) {

        if (ch != null && ch.isActive()) {
            ch.close();
        }
    }
}
