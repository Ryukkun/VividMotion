package foxy.ryukkun_.vividmotion.commands;

import foxy.ryukkun_.vividmotion.ConfigManager;
import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.imageutil.FFmpegSource;
import foxy.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bytedeco.javacv.FrameGrabber;

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
                    if (0.0 < d && d <= 20.0) {
                        ConfigManager.setFPS( d);
                    } else {
                        commandSender.sendMessage( "FPSは 下限0 上限20です。");
                    }
                    return true;
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


                FFmpegSource ffs = new FFmpegSource(input);
                if (ffs.can_load){
                    if (commandSender instanceof Player){
                        Player player = (Player) commandSender;
                        new ScreenData(name, ffs, player.getWorld());

                    } else if (commandSender instanceof BlockCommandSender) {
                        BlockCommandSender bcs = (BlockCommandSender) commandSender;
                        new ScreenData(name, ffs, bcs.getBlock().getWorld());

                    } else {
                        CMD.sendMessage(commandSender, " Player または CommandBlock から実行してください");
                    }

                }else {
                    CMD.sendMessage(commandSender, "解析不能なURL、PATHです。");
                    try {
                        ffs.ffg.close();
                    } catch (FrameGrabber.Exception e) {
                        throw new RuntimeException(e);
                    }
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
