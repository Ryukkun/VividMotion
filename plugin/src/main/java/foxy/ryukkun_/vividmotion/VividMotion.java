package foxy.ryukkun_.vividmotion;

import fox.ryukkun_.*;
import foxy.ryukkun_.vividmotion.commands.GiveScreen;
import foxy.ryukkun_.vividmotion.commands.SetScreen;
import foxy.ryukkun_.vividmotion.event.SelectSetUpScreen;
import foxy.ryukkun_.vividmotion.event.UsedSetUpScreen;
import foxy.ryukkun_.vividmotion.screen.ScreenData;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public final class VividMotion extends JavaPlugin {

    public static List<ScreenData> screenDataList = new ArrayList<>();
    public static boolean isEnable;
    public static PacketManager packetManager;
    public static MapUtil mapUtil;
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
        isEnable = true;
        packetManager = getPacketManager();
        mapUtil = getMapUtil();
        folder = getDataFolder();

        if (!getDataFolder().exists()){
            boolean ignored = getDataFolder().mkdirs();
        }

        File[] fs = getMapDataFolder().listFiles();
        for (File file : fs != null ? fs : new File[0]) {
            if (file.getName().endsWith(".dat")) {
                new ScreenData(file);
            }
        }

        getServer().getPluginManager().registerEvents(new UsedSetUpScreen(), this);
        getServer().getPluginManager().registerEvents(new SelectSetUpScreen(), this);
        getCommand("give-screen").setExecutor(new GiveScreen());
        getCommand("set-screen").setExecutor(new SetScreen());
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

        File[] fs =  file.listFiles();
        for (File file1 : fs != null ? fs : new File[0]){
            boolean ignored = file1.delete();
        }

        for (ScreenData mapsData : screenDataList){
            if (!mapsData.data.is_loaded){
                continue;
            }

            path1 = path.resolve(mapsData.data.name+ ".dat");
            try (FileOutputStream f = new FileOutputStream( path1.toFile());
                 ObjectOutputStream out = new ObjectOutputStream(f)){

                out.writeObject(mapsData.data);

            } catch (IOException e) {
                getLogger().warning(e.toString());
            }
        }
    }


    public static PacketManager getPacketManager(){
        NBTEditor.MinecraftVersion version = NBTEditor.getMinecraftVersion();
        if (version.equals(NBTEditor.MinecraftVersion.v1_12)){
            return new Packet_1_12();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_13)) {
            return new Packet_1_13();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_14)) {
            return new Packet_1_14();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_15)) {
            return new Packet_1_15();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_16)) {
            return new Packet_1_16();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_17)) {
            return new Packet_1_17();
//        } else if (version.equals(NBTEditor.MinecraftVersion.v1_18_R1)) {
//            return new Packet_1_18_R1();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_18_R2)) {
            return new Packet_1_18_R2();
//        } else if (version.equals(NBTEditor.MinecraftVersion.v1_19_R1)) {
//            return new Packet_1_19_R1();
//        } else if (version.equals(NBTEditor.MinecraftVersion.v1_19_R2)) {
//            return new Packet_1_19_R2();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_19_R3)) {
            return new Packet_1_19_R3();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_20)){
            return new Packet_1_20_R1();
        }
        return null;
    }


    public static MapUtil getMapUtil(){
        NBTEditor.MinecraftVersion version = NBTEditor.getMinecraftVersion();
        if (version.equals(NBTEditor.MinecraftVersion.v1_12)){
            return new MapUtil_1_12();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_13)) {
            return new MapUtil_1_13();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_14)) {
            return new MapUtil_1_14();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_15)) {
            return new MapUtil_1_15();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_16)) {
            return new MapUtil_1_16();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_17)) {
            return new MapUtil_1_17();
//        } else if (version.equals(NBTEditor.MinecraftVersion.v1_18_R1)) {
//            return new Packet_1_18_R1();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_18_R2)) {
            return new MapUtil_1_18();
//        } else if (version.equals(NBTEditor.MinecraftVersion.v1_19_R1)) {
//            return new Packet_1_19_R1();
//        } else if (version.equals(NBTEditor.MinecraftVersion.v1_19_R2)) {
//            return new Packet_1_19_R2();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_19_R3)) {
            return new MapUtil_1_19();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_20)){
            return new MapUtil_1_20_R1();
        }
        return null;
    }
}
