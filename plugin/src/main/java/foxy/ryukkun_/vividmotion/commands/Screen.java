package foxy.ryukkun_.vividmotion.commands;

import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.imageutil.FFmpegSource;
import foxy.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Screen extends SubCommandTemp{
    public Screen(){
        subCommands.put("set-fps", new SetFps());
        subCommands.put("resume", new Resume());
        subCommands.put("pause", new Pause());
        subCommands.put("delete", new Delete());
        subCommands.put("new", new New());
    }

    private static class SetFps extends ScreenCommandTemplate {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            if (2 <= args.length){
                ScreenData sc = VividMotion.getScreenData(args[0]);
                double d;
                try {
                    d = Double.parseDouble( args[1]);
                }catch ( NumberFormatException e) {
                    return false;
                }

                if (sc != null){
                    return sc.setFrameRate( d);
                }
            }
            return false;
        }
    }

    private static class Resume extends ScreenCommandTemplate {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            if (1 <= args.length){
                ScreenData sc = VividMotion.getScreenData(args[0]);

                if (sc != null){
                    sc.resume();
                    return true;
                }
            }
            return false;
        }
    }

    private static class Pause extends ScreenCommandTemplate {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            if (1 <= args.length){
                ScreenData sc = VividMotion.getScreenData(args[0]);

                if (sc != null){
                    sc.pause();
                    return true;
                }
            }
            return false;
        }
    }

    private static class Delete extends ScreenCommandTemplate {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            if (1 <= args.length){
                ScreenData sc = VividMotion.getScreenData(args[0]);

                if (sc != null){
                    sc.delete();
                    return true;
                }
            }
            return false;
        }
    }

    private static class New extends ScreenCommandTemplate {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            if (args.length < 2){
                return false;
            }

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

                Player player = (Player) commandSender;
                FFmpegSource ffs = new FFmpegSource(input);
                if (ffs.can_load){
                    new ScreenData(name, ffs, player);

                }else {
                    commandSender.sendMessage("解析不能なURL、PATHです。");
                }
            }).start();



            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
            return null;
        }
    }
}
