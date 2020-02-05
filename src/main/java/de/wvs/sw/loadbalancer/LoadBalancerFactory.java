package de.wvs.sw.loadbalancer;

import de.wvs.sw.loadbalancer.tcp.LoadBalancerSocket;
import de.wvs.sw.loadbalancer.udp.LoadBalancerDatagram;
import de.wvs.sw.loadbalancer.util.Mode;
import de.progme.iris.IrisConfig;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public final class LoadBalancerFactory {

    private LoadBalancerFactory() {
        // No instance
    }

    public static LoadBalancer create(Mode mode, IrisConfig irisConfig) {

        switch (mode) {
            case TCP:
                return new LoadBalancerSocket(irisConfig);
            case UDP:
                return new LoadBalancerDatagram(irisConfig);
            default:
                return new LoadBalancerSocket(irisConfig);
        }
    }
}
