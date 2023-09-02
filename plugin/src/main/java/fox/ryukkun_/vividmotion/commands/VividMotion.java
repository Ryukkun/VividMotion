package fox.ryukkun_.vividmotion.commands;

import fox.ryukkun_.vividmotion.ConfigManager;
import fox.ryukkun_.vividmotion.MCLogger;
import fox.ryukkun_.vividmotion.imageutil.ImageEncoder;
import fox.ryukkun_.vividmotion.screen.VideoPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VividMotion extends SubCommandTPL {
    public VividMotion() {
        subCommands.put("fps", new FPS());
        subCommands.put("map-encode", new MapEncode());
        subCommands.put("reload", new Reload());
        subCommands.put("show-screen-updates", new ShowScreenUpdates());
    }

    private static class FPS implements CMD {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            if (1 <= args.length) {
                try {
                    double d = Double.parseDouble(args[0]);
                    if (0.0 < d && d <= 20.0) {
                        ConfigManager.setFPS(d);
                        MCLogger.sendMessage(commandSender, MCLogger.Level.Success, "FPSを '" + d + "' に設定しました。");

                    } else {
                        MCLogger.sendMessage(commandSender, MCLogger.Level.Error, "FPSは 下限0 上限20です。");
                    }


                } catch (NumberFormatException e) {
                    MCLogger.sendMessage(commandSender, MCLogger.Level.Error, "数字に変換できませんでした");

                }

            } else {
                MCLogger.sendMessage(commandSender, MCLogger.Level.Error, "数字を入力してください");
            }
            return true;
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
                    ImageEncoder.EncodeType encode = ImageEncoder.EncodeType.nameOf(args[0]);
                    ConfigManager.setEncode(encode);
                    MCLogger.sendMessage(commandSender, MCLogger.Level.Success, "mapEncodeを '" + encode.name + "' に設定しました。");

                } catch (Exception e) {
                    MCLogger.sendMessage(commandSender, MCLogger.Level.Error, e.toString());
                }
                return true;
            }
            return false;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
            if (args.length == 1) {
                // screen name list
                List<String> tabList = new ArrayList<>();
                for (ImageEncoder.EncodeType v : ImageEncoder.EncodeType.values()) {
                    String name = v.name;

                    if (args[0].isEmpty() || name.startsWith(args[0])) {
                        tabList.add(name);
                    }
                }
                return tabList;
            }
            return null;
        }
    }


    private static class Reload implements CMD {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            ConfigManager.reload();
            MCLogger.sendMessage(commandSender, MCLogger.Level.Success, "Config Reloaded");
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            return null;
        }
    }


    private static class ShowScreenUpdates implements CMD {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
            if (!(commandSender instanceof Player)) {
                MCLogger.sendMessage(commandSender, MCLogger.Level.Error, "Please execute by the Player ");
                return true;
            }

            UUID uuid = ((Player)commandSender).getUniqueId();
            if (VideoPlayer.showUpdatePlayer.contains( uuid)) {
                VideoPlayer.showUpdatePlayer.remove( uuid);
                MCLogger.sendMessage(commandSender, MCLogger.Level.Success, "表示面の更新を表示 >> オン");

            } else {
                VideoPlayer.showUpdatePlayer.add( uuid);
                MCLogger.sendMessage(commandSender, MCLogger.Level.Success, "表示面の更新を表示 >> オフ");

            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            return null;
        }
    }
}