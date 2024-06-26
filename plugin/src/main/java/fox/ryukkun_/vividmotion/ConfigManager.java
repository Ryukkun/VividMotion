package fox.ryukkun_.vividmotion;

import fox.ryukkun_.vividmotion.imageutil.ImageEncoder;
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


    public static ImageEncoder.EncodeType getEncode() throws Exception {
        return ImageEncoder.EncodeType.nameOf( config.getString("mapEncode"));
    }

    public static void setEncode(ImageEncoder.EncodeType encode){
        config.set("mapEncode", encode.name);
        save();
    }

    public static byte getBackgroundColor() {
        return (byte)config.getInt("backgroundColor");
    }

    public static void setBackgroundColor(byte color) {
        config.set("backgroundColor", color);
        save();
    }

    public static void reload(){
        plugin.reloadConfig();
    }

    public static void save(){
        plugin.saveConfig();
    }
}
