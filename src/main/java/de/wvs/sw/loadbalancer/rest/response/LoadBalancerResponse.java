package de.wvs.sw.loadbalancer.rest.response;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class LoadBalancerResponse {

    private Status status;

    private String message;

    public LoadBalancerResponse(Status status, String message) {

        this.status = status;
        this.message = message;
    }

    public Status getStatus() {

        return status;
    }

    public String getMessage() {

        return message;
    }

    public enum Status {

        OK,
        SERVER_NOT_FOUND,
        SERVER_ALREADY_ADDED,
        ERROR
    }
}
