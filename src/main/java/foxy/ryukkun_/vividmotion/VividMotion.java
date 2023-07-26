package foxy.ryukkun_.vividmotion;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import foxy.ryukkun_.vividmotion.commands.Main;
import foxy.ryukkun_.vividmotion.videoutil.MapsData;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

//        manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.MAP) {
//            @Override
//            public void onPacketSending(PacketEvent event) {
//                PacketContainer packet = event.getPacket();
////                Byte col = packet.getBytes().readSafely(1);
////                Byte row = packet.getBytes().readSafely(2);
////                Byte x = packet.getBytes().readSafely(3);
////                Byte z = packet.getBytes().readSafely(4);
////                Integer length = packet.getIntegers().readSafely(2);
////                getLogger().info("columns:"+col+" rows:"+row+" x:"+x+" z:"+z+" length:"+length);
//                int i = 0;
//                byte[] old_array = packet.getByteArrays().read(0);
//                for (Object ob : packet.getModifier().getValues()) {
//                    if (i != 8) {
//                        getLogger().info(i + " : " + ob.toString());
//                    } else {
//                        getLogger().info(i + " : " + Arrays.toString(old_array));
//                    }
//                    i++;
//                }
//
//                i = 0;
//                List<Byte> new_array = new ArrayList<>();
//                for (int ii = 0; ii < old_array.length; ii++) {
//                    if (i == -48) {
//                        i = 0;
//                    }
//                    if (i == 128){
//                        i = -127;
//                    }
//                    new_array.add( (byte)i );
//                    i++;
//                }
//
//                packet.getByteArrays().write(0, ArrayUtils.toPrimitive(new_array.toArray(new Byte[0])));
//                getLogger().info("");
//            }
//        });
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
                e.printStackTrace();
            }
        }
    }
}
