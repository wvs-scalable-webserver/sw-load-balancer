package de.wvs.sw.loadbalancer.rest.response;

import de.wvs.sw.loadbalancer.util.BackendInfo;

import java.util.List;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class LoadBalancerListResponse extends LoadBalancerResponse {

    private List<BackendInfo> backendInfo;

    public LoadBalancerListResponse(Status status, String message, List<BackendInfo> backendInfo) {

        super(status, message);

        this.backendInfo = backendInfo;
    }
}
