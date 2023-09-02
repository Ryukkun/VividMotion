package fox.ryukkun_.vividmotion.commands;

import fox.ryukkun_.vividmotion.MCLogger;
import fox.ryukkun_.vividmotion.VividMotion;
import fox.ryukkun_.vividmotion.imageutil.FFmpegSource;
import fox.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Screen extends SubCommandTPL {
    public Screen(){
        subCommands.put("resume", new Resume());
        subCommands.put("pause", new Pause());
        subCommands.put("delete", new Delete());
        subCommands.put("new", new New());
    }

    private static class Resume extends ScreenCommandTPL {
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

    private static class Pause extends ScreenCommandTPL {
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

    private static class Delete extends ScreenCommandTPL {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            if (1 <= args.length){
                ScreenData sc = VividMotion.getScreenData(args[0]);

                if (sc != null){
                    sc.delete();
                    MCLogger.sendMessage(commandSender ,MCLogger.Level.Success ,args[0]+" の削除に成功しましたした。");

                } else {
                    MCLogger.sendMessage(commandSender ,MCLogger.Level.Error ,"Screenが見つかりませんでした。");
                }
                return true;
            }
            MCLogger.sendMessage(commandSender ,MCLogger.Level.Error ,"削除対象が入力されていません。");
            return true;
        }
    }

    private static class New extends ScreenCommandTPL {
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

                try {
                    FFmpegSource ffs = new FFmpegSource(input);
                    if (ffs.can_load) {
                        if (commandSender instanceof Player) {
                            Player player = (Player) commandSender;
                            new ScreenData(name, ffs, player.getWorld());

                        } else if (commandSender instanceof BlockCommandSender) {
                            BlockCommandSender bcs = (BlockCommandSender) commandSender;
                            new ScreenData(name, ffs, bcs.getBlock().getWorld());

                        } else {
                            MCLogger.syncSendMessage(commandSender, MCLogger.Level.Error, " Player または CommandBlock から実行してください");
                        }

                    } else {
                        MCLogger.syncSendMessage(commandSender, MCLogger.Level.Error, "解析不能なURL、PATHです。");
                        ffs.close();
                    }


                } catch (Exception e){
                    MCLogger.syncSendMessage(commandSender, MCLogger.Level.Error, e.getMessage());
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
