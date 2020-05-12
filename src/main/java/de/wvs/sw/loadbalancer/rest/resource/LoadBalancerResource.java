package de.wvs.sw.loadbalancer.rest.resource;

import com.google.gson.Gson;
import de.progme.hermes.server.http.annotation.method.DELETE;
import de.progme.hermes.server.http.annotation.method.POST;
import de.wvs.sw.loadbalancer.LoadBalancer;
import de.wvs.sw.loadbalancer.rest.response.LoadBalancerListResponse;
import de.wvs.sw.loadbalancer.rest.response.LoadBalancerResponse;
import de.wvs.sw.loadbalancer.rest.response.LoadBalancerStatsResponse;
import de.wvs.sw.loadbalancer.strategy.BalancingStrategy;
import de.wvs.sw.loadbalancer.task.CheckBackendTask;
import de.wvs.sw.loadbalancer.task.ConnectionsPerSecondTask;
import de.wvs.sw.loadbalancer.util.BackendInfo;
import de.progme.hermes.server.http.Request;
import de.progme.hermes.server.http.annotation.Path;
import de.progme.hermes.server.http.annotation.PathParam;
import de.progme.hermes.server.http.annotation.Produces;
import de.progme.hermes.server.http.annotation.method.GET;
import de.progme.hermes.shared.ContentType;
import de.progme.hermes.shared.http.Response;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
@Path("/loadbalancer")
public class LoadBalancerResource {

    private static final Response STATS_DISABLED;

    private static Logger logger = LoggerFactory.getLogger(LoadBalancerResource.class);

    private static Gson gson = new Gson();

    private static GlobalTrafficShapingHandler trafficShapingHandler = LoadBalancer.getInstance().getTrafficShapingHandler();

    private static ConnectionsPerSecondTask connectionsPerSecondTask = LoadBalancer.getInstance().getConnectionsPerSecondTask();

    static {
        STATS_DISABLED = Response.ok().content(gson.toJson(new LoadBalancerStatsResponse(LoadBalancerResponse.Status.ERROR,
                "Stats are disabled",
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1))).build();
    }

    @POST
    @Path("/add/{name}/{ip}/{port}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response add(Request httpRequest, @PathParam String name, @PathParam String ip, @PathParam String port) {

        BackendInfo found = null;
        for (BackendInfo info : LoadBalancer.getBackendTask().getBackends()) {
            if (info.getName().equalsIgnoreCase(name)) {
                found = info;
                break;
            }
        }

        if (found == null) {
            BackendInfo backend = new BackendInfo(name, ip, Integer.valueOf(port));
            LoadBalancer.getBalancingStrategy().addBackend(backend);
            LoadBalancer.getBackendTask().addBackend(backend);

            logger.info("Added backend server {}:{} to the load balancer", ip, port);

            return Response.ok().content(gson.toJson(new LoadBalancerResponse(LoadBalancerResponse.Status.OK,
                    "Successfully added server"))).build();
        } else {
            return Response.ok().content(gson.toJson(new LoadBalancerResponse(LoadBalancerResponse.Status.SERVER_ALREADY_ADDED,
                    "Server was already added"))).build();
        }
    }

    @DELETE
    @Path("/remove/{name}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response remove(Request httpRequest, @PathParam String name) {

        BackendInfo found = null;
        for (BackendInfo info : LoadBalancer.getBackendTask().getBackends()) {
            if (info.getName().equalsIgnoreCase(name)) {
                found = info;
                break;
            }
        }

        if (found != null) {
            LoadBalancer.getBalancingStrategy().removeBackend(found);
            LoadBalancer.getBackendTask().removeBackend(found);

            logger.info("Removed backend server {} from the load balancer", name);

            return Response.ok().content(gson.toJson(new LoadBalancerResponse(LoadBalancerResponse.Status.OK,
                    "Successfully removed server"))).build();
        } else {
            return Response.ok().content(gson.toJson(new LoadBalancerResponse(LoadBalancerResponse.Status.SERVER_NOT_FOUND,
                    "Server not found"))).build();
        }
    }

    @GET
    @Path("/list")
    @Produces(ContentType.APPLICATION_JSON)
    public Response list(Request httpRequest) {
        List<BackendInfo> backend = LoadBalancer.getBackendTask().getBackends();
        return Response
                .ok()
                .content(gson.toJson(new LoadBalancerListResponse(LoadBalancerResponse.Status.OK, "List received", backend)))
                .build();
    }

    @GET
    @Path("/stats")
    @Produces(ContentType.APPLICATION_JSON)
    public Response stats(Request httpRequest) {

        if (trafficShapingHandler != null) {
            TrafficCounter trafficCounter = trafficShapingHandler.trafficCounter();

            return Response.ok().content(gson.toJson(new LoadBalancerStatsResponse(LoadBalancerResponse.Status.OK,
                    "OK",
                    LoadBalancer.getChannelGroup().size(),
                    connectionsPerSecondTask.getPerSecond(),
                    LoadBalancer.getBalancingStrategy().getBackend().size(),
                    trafficCounter.currentReadBytes(),
                    trafficCounter.currentWrittenBytes(),
                    trafficCounter.lastReadThroughput(),
                    trafficCounter.lastWriteThroughput(),
                    trafficCounter.cumulativeReadBytes(),
                    trafficCounter.cumulativeWrittenBytes()))).build();
        } else {
            return STATS_DISABLED;
        }
    }
}
