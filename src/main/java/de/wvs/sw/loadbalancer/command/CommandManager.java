package de.wvs.sw.loadbalancer.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public class CommandManager {

    private Map<String, Command> commands = new HashMap<>();

    public Command findCommand(String name) {

        return (commands.containsKey(name)) ?
                commands.get(name) :
                commands.values().stream().filter((Command c) -> c.isValidAlias(name)).findFirst().orElse(null);
    }

    public void addCommand(Command command) {

        commands.put(command.getName(), command);
    }

    public void removeCommand(String command) {

        commands.remove(command);
    }

    public List<Command> getCommands() {

        return new ArrayList<>(commands.values());
    }
}
