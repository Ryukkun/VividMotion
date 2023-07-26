package foxy.ryukkun_.vividmotion.videoutil;

import foxy.ryukkun_.vividmotion.VividMotion;
import net.minecraft.server.v1_12_R1.PacketPlayOutMap;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class VideoPlayer extends Thread{

    public MapsData mapsData;
    public static HashMap<Integer, HashMap<UUID, Long>> lastTime = new HashMap<>();

    public VideoPlayer(MapsData mapsData){
        this.mapsData = mapsData;
    }

    public static void updateTime(int mapId, UUID uuid){
        if (!lastTime.containsKey(mapId)){
            lastTime.put(mapId, new HashMap<>());
        }
        lastTime.get(mapId).put(uuid, System.currentTimeMillis());
    }

    public static List<UUID> getPacketPlayer(int mapId){
        HashMap<UUID, Long> map = lastTime.get(mapId);
        if (map == null){
            return null;
        }

        long notTime = System.currentTimeMillis();
        List<UUID> uuids = new ArrayList<>();

        for (UUID uuid : map.keySet()){
            if (notTime - map.get(uuid) < 5000){
                uuids.add(uuid);
            }

        }
        return uuids;
    }



    @Override
    public void run() {
        // Check load
        if (!mapsData.data.is_loaded){
            if (mapsData.ffs == null){
                return;
            }

            mapsData.loadFFS();
        }

        // Send Packet Client
        List<UUID> uuids;
        int i = -1;
        int frame;
        long next = System.currentTimeMillis();
        long start = System.currentTimeMillis();

        try{
            while (VividMotion.isEnable){

                // calc frame
                frame = (int)((next - start) % 1000 * mapsData.data.videoFrameRate / 1000);

                // Send Packets
                byte[][] pixelData = mapsData.getMapData(frame);
                for (int mapId : mapsData.data.mapIds){
                    uuids = getPacketPlayer(mapId);
                    i++;

                    if (uuids != null){
                        PacketPlayOutMap packet = new PacketPlayOutMap(mapId, (byte) 0, true, new ArrayList<>(), pixelData[i], 0, 0, 128, 128);
                        for (UUID uuid : uuids){
                            ((CraftPlayer)Bukkit.getPlayer(uuid)).getHandle().playerConnection.sendPacket(packet);
                        }
                    }
                }

                // sleep
                next += 1000 / mapsData.getFrameRate();
                Thread.sleep(Math.max(0,  System.currentTimeMillis() - next));

                }
            } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
