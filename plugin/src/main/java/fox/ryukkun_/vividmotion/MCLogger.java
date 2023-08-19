package fox.ryukkun_.vividmotion;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MCLogger {

    private static final String ErrorPrefix = ChatColor.DARK_RED + "V" + ChatColor.RED + "ivid" +
            ChatColor.DARK_RED + "M" + ChatColor.RED + "otion " +
            ChatColor.DARK_RED + ">> " + ChatColor.RED;

    private static final String WarningPrefix = ChatColor.GOLD + "V" + ChatColor.YELLOW + "ivid" +
            ChatColor.GOLD + "M" + ChatColor.YELLOW + "otion " +
            ChatColor.GOLD + ">> " + ChatColor.YELLOW;

    private static final String SuccessPrefix = ChatColor.DARK_GREEN + "V" + ChatColor.GREEN + "ivid" +
            ChatColor.DARK_GREEN + "M" + ChatColor.GREEN + "otion " +
            ChatColor.DARK_GREEN + ">> " + ChatColor.GREEN;

    private static final String WhitePrefix = "VividMotion >> ";


    public static void sendMessage(Player player, Level level, String text) {
        player.sendMessage( getMessage( text, level));
    }

    public static void sendMessage(CommandSender sender, Level level ,String text) {
        sender.sendMessage( getMessage( text, level));
    }

    public static void syncSendMessage(CommandSender sender, Level level ,String text) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendMessage(sender, level, text);
            }
        }.runTask( VividMotion.plugin);
    }

    public static void syncSendMessage(Player player, Level level ,String text) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendMessage(player, level, text);
            }
        }.runTask( VividMotion.plugin);
    }

    private static String getMessage(String text, Level level) {
        if (level.equals( Level.Error)) {
            return ErrorPrefix+text;
        } else if (level.equals( Level.Warning)) {
            return WarningPrefix+text;
        } else if (level.equals( Level.Success)) {
            return SuccessPrefix+text;
        } else {
            return WhitePrefix+text;
        }
    }

    public enum Level {
        Error, Warning, Success, White
    }
}
