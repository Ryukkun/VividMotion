package fox.ryukkun_.vividmotion.commands;

import fox.ryukkun_.vividmotion.MCLogger;
import fox.ryukkun_.vividmotion.VividMotion;
import fox.ryukkun_.vividmotion.imageutil.FFmpegSource;
import fox.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bytedeco.javacv.FrameGrabber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScreenCommandTPL implements CMD {

    public void onCommandInCache(Player player, ScreenData screenData){}
    public void onCommandNotInCache(Player player, ScreenData screenData){}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (!(commandSender instanceof Player)){
            MCLogger.sendMessage(commandSender, MCLogger.Level.Error, "Please execute by the Player ");
            return true;
        }
        Player player = (Player) commandSender;

        if (1 == args.length){
            // in cache
            ScreenData screenData = VividMotion.getScreenData( args[0]);
            if (screenData != null){
                onCommandInCache(player, screenData);
                return true;
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
                    MCLogger.syncSendMessage(player, MCLogger.Level.Error, "解析不能なURL、PATHです。");
                    try {
                        ffs.ffg.close();
                    } catch (FrameGrabber.Exception e) {}
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
