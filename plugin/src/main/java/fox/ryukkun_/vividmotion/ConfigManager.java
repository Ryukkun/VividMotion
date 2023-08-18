package fox.ryukkun_.vividmotion;

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
        if (encode.trim().equalsIgnoreCase( "誤差拡散")) {
            config.set("mapEncode", "誤差拡散");
        } else if (encode.trim().equalsIgnoreCase( "誤差拡散.mk3")) {
            config.set("mapEncode", "誤差拡散.Mk3");
        } else {
            return;
        }

        save();
    }

    public static void reload(){
        plugin.reloadConfig();
    }

    public static void save(){
        plugin.saveConfig();
    }
}
