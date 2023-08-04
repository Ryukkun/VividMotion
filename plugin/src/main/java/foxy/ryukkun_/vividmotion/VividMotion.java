package foxy.ryukkun_.vividmotion;

import fox.ryukkun_.*;
import foxy.ryukkun_.vividmotion.commands.GiveScreen;
import foxy.ryukkun_.vividmotion.commands.SetScreen;
import foxy.ryukkun_.vividmotion.screen.ScreenData;
import io.github.bananapuncher714.nbteditor.NBTEditor;
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

    public File getMapDataFolder(){
        Path path = getDataFolder().toPath();
        path = path.resolve("MapData");
        File file = path.toFile();
        boolean ignored = file.mkdirs();
        return file;
    }



    @Override
    public void onEnable() {
        // Plugin startup logic
        isEnable = true;
        packetManager = getPacketManager();
        mapUtil = getMapUtil();

        if (!getDataFolder().exists()){
            boolean ignored = getDataFolder().mkdirs();
        }

        File[] fs = getMapDataFolder().listFiles();
        for (File file : fs != null ? fs : new File[0]) {
            screenDataList.add( new ScreenData(file));
        }

        getCommand("give-screen").setExecutor(new GiveScreen());
        getCommand("set-screen").setExecutor(new SetScreen());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        isEnable = false;
        saveMapData();

    }

    public void saveMapData(){
        File file = getMapDataFolder();
        Path path = file.toPath();
        Path path1;
        int[] ids;

        File[] fs =  file.listFiles();
        for (File file1 : fs != null ? fs : new File[0]){
            boolean ignored = file1.delete();
        }

        for (ScreenData mapsData : screenDataList){
            ids = mapsData.data.mapIds;
            path1 = path.resolve( ids[0] + "-" + ids[ids.length - 1] + ".dat");

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
