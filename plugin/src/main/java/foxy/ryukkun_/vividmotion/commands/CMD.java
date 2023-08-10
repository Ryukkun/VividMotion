package foxy.ryukkun_.vividmotion.commands;

import foxy.ryukkun_.vividmotion.VividMotion;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;

public interface CMD extends CommandExecutor, TabCompleter {

    static void sendMessage(CommandSender commandSender, String message){
        new BukkitRunnable() {
            @Override
            public void run() {
                commandSender.sendMessage(message);
            }
        }.runTask(VividMotion.plugin);
    }

}
