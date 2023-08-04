package foxy.ryukkun_.vividmotion.commands;

import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScreenCommandTemplate implements CommandExecutor, TabCompleter {

    public void onCommandInCache(Player player, ScreenData screenData){}
    public void onCommandNotInCache(Player player, String name, String input){}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        Player player = (Player) commandSender;

        if (1 == args.length){
            // in cache
            for (ScreenData screenData : VividMotion.screenDataList){
                if (screenData.data.name.equals(args[0])){
                    onCommandInCache(player, screenData);
                    return true;
                }
            }
            return false;


        } else if (2 <= args.length) {
            // create new screen
            String input;

            if (3 <= args.length) {
                input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }else{
                input = args[1];
            }

            onCommandNotInCache(player, args[0], input);


            return true;
        }


        return false;
    }



    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            // screen name list
            List<String> tabList = new ArrayList<>();
            for (ScreenData screenData : VividMotion.screenDataList) {
                String name = screenData.data.name;

                if (args[0].isEmpty() || name.startsWith(args[0])) {
                    tabList.add(name);
                }
            }
            return tabList;
        }
        return null;
    }
}
