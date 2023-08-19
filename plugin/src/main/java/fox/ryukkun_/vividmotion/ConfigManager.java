package fox.ryukkun_.vividmotion;

import fox.ryukkun_.vividmotion.imageutil.ImageConverter;
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


    public static ImageConverter.EncodeType getEncode() throws Exception {
        return ImageConverter.EncodeType.nameOf( config.getString("mapEncode"));
    }

    public static void setEncode(ImageConverter.EncodeType encode){
        config.set("mapEncode", encode.name);
        save();
    }

    public static void reload(){
        plugin.reloadConfig();
    }

    public static void save(){
        plugin.saveConfig();
    }
}
