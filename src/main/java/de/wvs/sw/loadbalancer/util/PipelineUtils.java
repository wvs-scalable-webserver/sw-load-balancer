package de.wvs.sw.loadbalancer.util;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.PlatformDependent;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public final class PipelineUtils {

    public static final int DEFAULT_THREADS_THRESHOLD = 1;

    public static final int DEFAULT_BOSS_THREADS = 1;

    public static final int DEFAULT_WORKER_THREADS = Runtime.getRuntime().availableProcessors() * 2;

    private static boolean epoll;

    static {
        if (!PlatformDependent.isWindows()) {
            epoll = Epoll.isAvailable();
        }
    }

    private PipelineUtils() {
        // No instance
    }

    public static EventLoopGroup newEventLoopGroup(int threads) {

        return epoll ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);
    }

    public static Class<? extends ServerChannel> getServerChannel() {

        return epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends Channel> getChannel() {

        return epoll ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends Channel> getDatagramChannel() {

        return epoll ? EpollDatagramChannel.class : NioDatagramChannel.class;
    }

    public static boolean isEpoll() {

        return epoll;
    }
}
