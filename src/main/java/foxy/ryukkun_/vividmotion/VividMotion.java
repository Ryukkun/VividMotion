package foxy.ryukkun_.vividmotion;

import foxy.ryukkun_.vividmotion.commands.Main;
import foxy.ryukkun_.vividmotion.videoutil.MapsData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public final class VividMotion extends JavaPlugin {

    public static List<MapsData> mapsDataList = new ArrayList<>();
    public static boolean isEnable;

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
        if (!getDataFolder().exists()){
            boolean ignored = getDataFolder().mkdirs();
        }

        File[] fs = getMapDataFolder().listFiles();
        for (File file : fs != null ? fs : new File[0]) {
            mapsDataList.add( new MapsData(file));
        }



        getCommand("vividmotion").setExecutor(new Main());
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

        for (MapsData mapsData : mapsDataList){
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
}
