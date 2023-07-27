package foxy.ryukkun_.vividmotion.videoutil;

import foxy.ryukkun_.vividmotion.VividMotion;
import net.minecraft.server.v1_12_R1.PacketPlayOutMap;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

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
        } else if (map.isEmpty()) {
            return null;
        }

        long notTime = System.currentTimeMillis();
        List<UUID> uuids = new ArrayList<>();

        for (UUID uuid : map.keySet()){
            if (notTime - map.get(uuid) < 2000){
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

        // Set MapDetector
        MapView view = Bukkit.getMap((short) mapsData.data.mapIds[0]);
        view.getRenderers().clear();
        view.addRenderer(new MapDetector());


        // Send Packet Client
        List<UUID> uuids;
        int i = -1;
        int frame;
        long next = mapsData.data.nowFrame <= 0 ? System.currentTimeMillis() : System.currentTimeMillis() + (long)(1000 / mapsData.data.videoFrameRate * mapsData.data.nowFrame);
        long start = System.currentTimeMillis();

        try{
            while (VividMotion.isEnable){

                // check need update
                uuids = getPacketPlayer(mapsData.data.mapIds[0]);
                if (uuids != null){

                    // calc frame
                    // next - start / 1000 * vFrameRate = nowFrame
                    frame = (int) ((next - start) * mapsData.data.videoFrameRate / 1000) % mapsData.data.map_pixel.size();

                    // Send Packets
                    byte[][] pixelData = mapsData.getMapData(frame);
                    for (int mapId : mapsData.data.mapIds){
                        i++;

                        PacketPlayOutMap packet = new PacketPlayOutMap(mapId, (byte) 0, true, new ArrayList<>(), pixelData[i], 0, 0, 128, 128);
                        for (UUID uuid : uuids){
                            ((CraftPlayer)Bukkit.getPlayer(uuid)).getHandle().playerConnection.sendPacket(packet);
                        }
                    }
                }

                // sleep
                next += (long) (1000 / mapsData.getFrameRate());
                Thread.sleep(Math.max(0,  next - System.currentTimeMillis()));

                }
            } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static class MapDetector extends MapRenderer {

        @Override
        public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
            updateTime(mapView.getId(), player.getUniqueId());
        }
    }
}
