package de.wvs.sw.loadbalancer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class BackendInfo {

    public static final int DEFAULT_TCP_TIMEOUT = 4000;

    public static final int DEFAULT_UDP_TIMEOUT = 1500;

    public static final byte[] EMPTY_BUFFER = new byte[] {};

    public static final DatagramPacket EMPTY_PACKET = new DatagramPacket(EMPTY_BUFFER, EMPTY_BUFFER.length);

    private static Logger logger = LoggerFactory.getLogger(BackendInfo.class);

    private String name;

    private String host;

    private int port;

    private double connectTime;

    public BackendInfo(String name, String host, int port) {

        this.name = name;
        this.host = host;
        this.port = port;
    }

    public String getName() {

        return name;
    }

    public String getHost() {

        return host;
    }

    public int getPort() {

        return port;
    }

    public double getConnectTime() {

        return connectTime;
    }

    public boolean checkSocket() {

        boolean online = false;

        try (Socket socket = new Socket()) {
            long now = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(host, port), DEFAULT_TCP_TIMEOUT);
            connectTime = System.currentTimeMillis() - now;

            online = true;
        } catch (IOException ignore) {}

        return online;
    }

    public boolean checkDatagram() {

        boolean online = false;

        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.setSoTimeout(DEFAULT_UDP_TIMEOUT);
            long now = System.currentTimeMillis();
            datagramSocket.connect(new InetSocketAddress(host, port));
            datagramSocket.send(EMPTY_PACKET);
            datagramSocket.receive(EMPTY_PACKET);
            connectTime = System.currentTimeMillis() - now;

            online = true;
        } catch (Exception ignore) {}

        return online;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BackendInfo that = (BackendInfo) o;

        return port == that.port &&
                (name != null ? name.equals(that.name) : that.name == null &&
                        (host != null ? host.equals(that.host) : that.host == null));
    }

    @Override
    public int hashCode() {

        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;

        return result;
    }

    @Override
    public String toString() {

        return "BackendInfo{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", connectTime=" + connectTime +
                '}';
    }
}
