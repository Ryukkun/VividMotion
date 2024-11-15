package fox.ryukkun_.vividmotion;

import fox.ryukkun_.MapGetter;
import fox.ryukkun_.MapUtil_1_12_R1;
import fox.ryukkun_.MapUtil_1_13_R2;
import fox.ryukkun_.vividmotion.commands.GiveScreen;
import fox.ryukkun_.vividmotion.commands.Screen;
import fox.ryukkun_.vividmotion.commands.SetScreen;
import fox.ryukkun_.vividmotion.event.BreakScreen;
import fox.ryukkun_.vividmotion.event.InteractScreen;
import fox.ryukkun_.vividmotion.event.SelectSetUpScreen;
import fox.ryukkun_.vividmotion.event.UsedSetUpScreen;
import fox.ryukkun_.vividmotion.imageutil.ImageEncoder;
import fox.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public final class VividMotion extends JavaPlugin {

    public static final List<ScreenData> screenDataList = new ArrayList<>();
    public static boolean isEnable;
    public static MapGetter mapGetter;
    public static Plugin plugin;
    private static File folder;


    public static File getMapDataFolder(){
        Path path = folder.toPath();
        path = path.resolve("MapData");
        File file = path.toFile();
        boolean ignored = file.mkdirs();
        return file;
    }


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        ConfigManager.setPlugin( this);
        isEnable = true;
        mapGetter = getMapGetter();
        folder = getDataFolder();
        ImageEncoder.load();


        if (!getDataFolder().exists()){
            boolean ignored = getDataFolder().mkdirs();
        }

        File[] fs = getMapDataFolder().listFiles();
        for (File file : fs != null ? fs : new File[0]) {

            if (file.isDirectory()){
                File[] fs1 = file.listFiles();

                for (File file1 : fs1 != null ? fs1 : new File[0]) {
                    if (file1.getName().endsWith("screen.dat")) {
                        new ScreenData(file1);
                    }
                }
            }
        }

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new UsedSetUpScreen(), this);
        pluginManager.registerEvents(new SelectSetUpScreen(), this);
        pluginManager.registerEvents(new BreakScreen(), this);
        if (!MCVersion.isNewerThan(MCVersion.v1_16_R1)) {
            pluginManager.registerEvents(new InteractScreen(), this);
        }


        getCommand("give-screen").setExecutor(new GiveScreen());
        getCommand("set-screen").setExecutor(new SetScreen());
        getCommand("screen").setExecutor(new Screen());
        getCommand("vividmotion").setExecutor(new fox.ryukkun_.vividmotion.commands.VividMotion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        isEnable = false;
        saveMapData();

    }


    public static ScreenData getScreenData(String name){
        for (ScreenData sd : VividMotion.screenDataList) {
            if (sd.data.name.equals(name)) {
                return sd;
            }
        }
        return null;
    }

    public static ScreenData getScreenData(int mapId){
        for (ScreenData sd : VividMotion.screenDataList) {
            for (int _id : sd.data.mapIds) {
                if (mapId == _id) return sd;
            }
        }
        return null;
    }


    public void saveMapData(){
        File file = getMapDataFolder();
        Path path = file.toPath();
        Path path1;


        for (ScreenData mapsData : screenDataList){
            if (!mapsData.data.is_loaded){
                continue;
            }

            path1 = path.resolve(mapsData.data.name);
            path1.toFile().mkdirs();
            path1 = path1.resolve("screen.dat");
            try (FileOutputStream f = new FileOutputStream( path1.toFile());
                 ObjectOutputStream out = new ObjectOutputStream(f)){

                out.writeObject(mapsData.data);

            } catch (IOException e) {
                getLogger().warning(e.toString());
            }
        }
    }



    private static MapGetter getMapGetter(){
        if (MCVersion.isNewerThan( MCVersion.v1_13_R2)) {
            return new MapUtil_1_13_R2();
        } else {
            return new MapUtil_1_12_R1();
        }
    }
}