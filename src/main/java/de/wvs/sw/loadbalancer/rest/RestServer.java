package de.wvs.sw.loadbalancer.rest;

import de.progme.hermes.server.HermesServer;
import de.progme.hermes.server.HermesServerFactory;
import de.progme.iris.IrisConfig;
import de.progme.iris.config.Header;
import de.progme.iris.config.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class RestServer {

    private static Logger logger = LoggerFactory.getLogger(RestServer.class);

    private IrisConfig irisConfig;

    private HermesServer hermesServer;

    public RestServer(IrisConfig irisConfig) {

        this.irisConfig = irisConfig;
    }

    public void start() {

        Header restHeader = irisConfig.getHeader("rest");
        Key serverKey = restHeader.getKey("server");

        String ip = serverKey.getValue(0).asString();
        int port = serverKey.getValue(1).asInt();

        hermesServer = HermesServerFactory.create(new LoadBalancerRestConfig(ip, port));
        hermesServer.start();

        logger.info("RESTful API listening on {}:{}", ip, port);
    }

    public void stop() {

        hermesServer.stop();
    }
}
