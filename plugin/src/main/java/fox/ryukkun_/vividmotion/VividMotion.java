package fox.ryukkun_.vividmotion;

import fox.ryukkun_.*;
import fox.ryukkun_.vividmotion.commands.GiveScreen;
import fox.ryukkun_.vividmotion.commands.Screen;
import fox.ryukkun_.vividmotion.commands.SetScreen;
import fox.ryukkun_.vividmotion.event.SelectSetUpScreen;
import fox.ryukkun_.vividmotion.event.UsedSetUpScreen;
import fox.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public final class VividMotion extends JavaPlugin {

    public static final List<ScreenData> screenDataList = new ArrayList<>();
    public static boolean isEnable;
    public static PacketManager packetManager;
    public static MapUtil mapUtil;
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
        ConfigManager.setPlugin( this);
        isEnable = true;
        packetManager = getPacketManager();
        mapUtil = getMapUtil();
        mapGetter = getMapGetter();
        folder = getDataFolder();


        if (!getDataFolder().exists()){
            boolean ignored = getDataFolder().mkdirs();
        }

        File[] fs = getMapDataFolder().listFiles();
        for (File file : fs != null ? fs : new File[0]) {

            if (file.isDirectory()){
                File[] fs1 = file.listFiles();

                for (File file1 : fs1 != null ? fs1 : new File[0]) {
                    if (file1.getName().endsWith(".dat")) {
                        new ScreenData(file1);
                    }
                }
            }
        }

        getServer().getPluginManager().registerEvents(new UsedSetUpScreen(), this);
        getServer().getPluginManager().registerEvents(new SelectSetUpScreen(), this);
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
            path1 = path1.resolve(mapsData.data.name+ ".dat");
            try (FileOutputStream f = new FileOutputStream( path1.toFile());
                 ObjectOutputStream out = new ObjectOutputStream(f)){

                out.writeObject(mapsData.data);

            } catch (IOException e) {
                getLogger().warning(e.toString());
            }
        }
    }



    private static MapGetter getMapGetter(){
    if (MCVersion.equal( MCVersion.v1_12_R1)) {
        return new MapUtil_1_12_R1();
    } else if (MCVersion.equal( MCVersion.v1_13_R1)) {
        return new MapUtil_1_13_R1();
    } else {
            return new MapUtil_1_13_R2();
        }
    }





    private static PacketManager getPacketManager(){
        if (MCVersion.equal( MCVersion.v1_12_R1)){
            return new Packet_1_12_R1();
        } else if (MCVersion.equal( MCVersion.v1_13_R1)) {
            return new Packet_1_13_R1();
        } else if (MCVersion.equal( MCVersion.v1_13_R2)) {
            return new Packet_1_13_R2();
        } else if (MCVersion.equal( MCVersion.v1_14_R1)) {
            return new Packet_1_14_R1();
        } else if (MCVersion.equal( MCVersion.v1_15_R1)) {
            return new Packet_1_15_R1();
        } else if (MCVersion.equal( MCVersion.v1_16_R1)) {
            return new Packet_1_16_R1();
        } else if (MCVersion.equal( MCVersion.v1_16_R2)) {
            return new Packet_1_16_R2();
        } else if (MCVersion.equal( MCVersion.v1_16_R3)) {
            return new Packet_1_16_R3();
        } else if (MCVersion.equal( MCVersion.v1_17_R1)) {
            return new Packet_1_17_R1();
        } else if (MCVersion.equal( MCVersion.v1_18_R1)) {
            return new Packet_1_18_R1();
        } else if (MCVersion.equal( MCVersion.v1_18_R2)) {
            return new Packet_1_18_R2();
        } else if (MCVersion.equal( MCVersion.v1_19_R1)) {
            return new Packet_1_19_R1();
        } else if (MCVersion.equal( MCVersion.v1_19_R2)) {
            return new Packet_1_19_R2();
        } else if (MCVersion.equal( MCVersion.v1_19_R3)) {
            return new Packet_1_19_R3();
        } else if (MCVersion.equal( MCVersion.v1_20_R1)){
            return new Packet_1_20_R1();
        }
        return null;
    }


    private static MapUtil getMapUtil(){
        if (MCVersion.equal( MCVersion.v1_12_R1)){
            return new MapUtil_1_12_R1();
        } else if (MCVersion.equal( MCVersion.v1_13_R1)) {
            return new MapUtil_1_13_R1();
        } else if (MCVersion.equal( MCVersion.v1_13_R2)) {
            return new MapUtil_1_13_R2();
        } else if (MCVersion.equal( MCVersion.v1_14_R1)) {
            return new MapUtil_1_14_R1();
        } else if (MCVersion.equal( MCVersion.v1_15_R1)) {
            return new MapUtil_1_15_R1();
        } else if (MCVersion.equal( MCVersion.v1_16_R1)) {
            return new MapUtil_1_16_R1();
        } else if (MCVersion.equal( MCVersion.v1_16_R2)) {
            return new MapUtil_1_16_R2();
        } else if (MCVersion.equal( MCVersion.v1_16_R3)) {
            return new MapUtil_1_16_R3();
        } else if (MCVersion.equal( MCVersion.v1_17_R1)) {
            return new MapUtil_1_17_R1();
        } else if (MCVersion.equal( MCVersion.v1_18_R1)) {
            return new MapUtil_1_18_R1();
        } else if (MCVersion.equal( MCVersion.v1_18_R2)) {
            return new MapUtil_1_18_R2();
        } else if (MCVersion.equal( MCVersion.v1_19_R1)) {
            return new MapUtil_1_19_R1();
        } else if (MCVersion.equal( MCVersion.v1_19_R2)) {
            return new MapUtil_1_19_R2();
        } else if (MCVersion.equal( MCVersion.v1_19_R3)) {
            return new MapUtil_1_19_R3();
        } else if (MCVersion.equal( MCVersion.v1_20_R1)){
            return new MapUtil_1_20_R1();
        }
        return null;
    }
}