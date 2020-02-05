package de.wvs.sw.loadbalancer.rest;

import de.wvs.sw.loadbalancer.rest.resource.LoadBalancerResource;
import de.progme.hermes.server.impl.HermesConfig;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class LoadBalancerRestConfig extends HermesConfig {

    public LoadBalancerRestConfig(String host, int port) {

        host(host);
        port(port);
        corePoolSize(2);
        maxPoolSize(4);
        backLog(50);
        register(LoadBalancerResource.class);
    }
}
