package fox.ryukkun_.vividmotion.commands;

import fox.ryukkun_.vividmotion.ConfigManager;
import fox.ryukkun_.vividmotion.imageutil.ImageConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VividMotion extends SubCommandTPL {
    public VividMotion(){
        subCommands.put("fps", new FPS());
        subCommands.put("map-encode", new MapEncode());
        subCommands.put("reload", new Reload());
    }

    private static class FPS implements CMD {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            if (1 <= args.length) {
                try {
                    double d = Double.parseDouble(args[0]);
                    if (0.0 < d && d <= 20.0) {
                        ConfigManager.setFPS(d);
                    } else {
                        commandSender.sendMessage("FPSは 下限0 上限20です。");
                    }
                    return true;

                } catch (NumberFormatException e) {}
            }
            return false;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            return null;
        }
    }

    private static class MapEncode implements CMD {

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            if (1 <= args.length) {
                try {
                    ImageConverter.EncodeType encode = ImageConverter.EncodeType.nameOf( args[0]);
                    ConfigManager.setEncode( encode);

                } catch (Exception e) {
                    commandSender.sendMessage( e.toString());
                }
                return true;
            }
            return false;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            return null;
        }
    }


    private static class Reload implements CMD {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            ConfigManager.reload();
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            return null;
        }
    }
}
