package de.wvs.sw.loadbalancer.rest.response;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
@SuppressWarnings("FieldCanBeLocal")
public class LoadBalancerStatsResponse extends LoadBalancerResponse {

    private int connections;

    private int connectionsPerSecond;

    private int onlineBackendServers;

    private long currentReadBytes;

    private long currentWrittenBytes;

    private long lastReadThroughput;

    private long lastWriteThroughput;

    private long totalReadBytes;

    private long totalWrittenBytes;

    public LoadBalancerStatsResponse(Status status, String message, int connections, int connectionsPerSecond, int onlineBackendServers, long currentReadBytes, long currentWrittenBytes, long lastReadThroughput, long lastWriteThroughput, long totalReadBytes, long totalWrittenBytes) {

        super(status, message);

        this.connections = connections;
        this.connectionsPerSecond = connectionsPerSecond;
        this.onlineBackendServers = onlineBackendServers;
        this.currentReadBytes = currentReadBytes;
        this.currentWrittenBytes = currentWrittenBytes;
        this.lastReadThroughput = lastReadThroughput;
        this.lastWriteThroughput = lastWriteThroughput;
        this.totalReadBytes = totalReadBytes;
        this.totalWrittenBytes = totalWrittenBytes;
    }
}
