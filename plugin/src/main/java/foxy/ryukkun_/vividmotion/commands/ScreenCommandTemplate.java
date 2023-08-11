package foxy.ryukkun_.vividmotion.commands;

import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.imageutil.FFmpegSource;
import foxy.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScreenCommandTemplate implements CMD {

    public void onCommandInCache(Player player, ScreenData screenData){}
    public void onCommandNotInCache(Player player, ScreenData screenData){}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage("Please execute by the Player ");
            return true;
        }
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
            String name = args[0];
            String input;
            if (VividMotion.getScreenData( name) != null){
                return false;
            }

            if (3 <= args.length) {
                input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }else{
                input = args[1];
            }

            new Thread(() -> {

                FFmpegSource ffs = new FFmpegSource(input);
                if (ffs.can_load){
                    ScreenData sd = new ScreenData(name, ffs, player.getWorld());
                    onCommandNotInCache(player, sd);

                }else {
                    player.sendMessage("解析不能なURL、PATHです。");
                }
            }).start();



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
