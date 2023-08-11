package foxy.ryukkun_.vividmotion;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
    private static Plugin plugin;
    private static FileConfiguration config;
    public static void setPlugin(Plugin plugin) {
        ConfigManager.plugin = plugin;
        config = plugin.getConfig();
    }

    public static FileConfiguration get(){
        return config;
    }


    public static double getFPS(){
        return config.getDouble("fps");
    }

    public static void setFPS(double fps){
        config.set("fps", fps);
        save();
    }


    public static String getEncode(){
        return config.getString("mapEncode");
    }

    public static void setEncode(String encode){
        config.set("mapEncode", encode);
        save();
    }

    public static void reload(){
        plugin.reloadConfig();
    }

    public static void save(){
        plugin.saveConfig();
    }
}
