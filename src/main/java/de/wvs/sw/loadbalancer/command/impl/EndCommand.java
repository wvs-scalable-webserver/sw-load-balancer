package de.wvs.sw.loadbalancer.command.impl;

import de.wvs.sw.loadbalancer.LoadBalancer;
import de.wvs.sw.loadbalancer.command.Command;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class EndCommand extends Command {

    public EndCommand(String name, String description, String... aliases) {

        super(name, description, aliases);
    }

    @Override
    public boolean execute(String[] args) {

        LoadBalancer.getInstance().stop();

        return true;
    }
}
