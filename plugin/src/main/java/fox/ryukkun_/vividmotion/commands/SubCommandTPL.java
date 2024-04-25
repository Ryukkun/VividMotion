package fox.ryukkun_.vividmotion.commands;

import fox.ryukkun_.vividmotion.MCLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SubCommandTPL implements CMD {
    final HashMap<String, CMD> subCommands = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (1 <= strings.length){
            if (subCommands.containsKey(strings[0])){
                try {
                    return subCommands.get(strings[0]).onCommand(commandSender, command, s, Arrays.copyOfRange(strings, 1, strings.length));

                } catch (Exception e) {
                    MCLogger.sendMessage(commandSender, MCLogger.Level.Error, e.getMessage());
                    e.printStackTrace();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (1 == strings.length){
            List<String> tabList = new ArrayList<>();
            for (String name : subCommands.keySet()) {

                if (strings[0].isEmpty() || name.startsWith(strings[0])) {
                    tabList.add(name);
                }
            }
            return tabList;

        }else if (2 <= strings.length){
            if (subCommands.containsKey(strings[0])){
                return subCommands.get(strings[0]).onTabComplete(commandSender, command, s, Arrays.copyOfRange(strings, 1, strings.length));
            }
        }
        return null;
    }


}
