package foxy.ryukkun_.vividmotion.screen;

import foxy.ryukkun_.vividmotion.VividMotion;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.*;

public class VideoPlayer extends Thread{

    public ScreenData mapsData;
    public static HashMap<Integer, HashMap<UUID, Long>> lastTime = new HashMap<>();

    public VideoPlayer(ScreenData mapsData){
        this.mapsData = mapsData;
    }

    public static void updateTime(int mapId, UUID uuid){
        if (!lastTime.containsKey(mapId)){
            lastTime.put(mapId, new HashMap<>());
        }
        lastTime.get(mapId).put(uuid, System.currentTimeMillis());
    }

    public static List<UUID> getPacketNeeded(int mapId){
        HashMap<UUID, Long> map = lastTime.get(mapId);
        List<UUID> uuids = new ArrayList<>();
        if (map == null){
            return uuids;
        } else if (map.isEmpty()) {
            return uuids;
        }

        long notTime = System.currentTimeMillis();


        for (UUID uuid : map.keySet()){
            if (notTime - map.get(uuid) < 10000){
                uuids.add(uuid);
            }

        }
        return uuids;
    }



    @Override
    public void run() {
        // Send Packet Client
        long next = System.currentTimeMillis();
        List<UUID> alreadySend = new ArrayList<>();
        byte[][] lastPixelData = null;

        try{
            while (VividMotion.isEnable && mapsData.loopEnable){

                if (mapsData.isPausing()){
                    // if Pausing
                    // Send Packets
                    byte[][] pixelData = mapsData.getMapData( mapsData.data.nowFrame);
                    List<UUID> uuids = getPacketNeeded( mapsData.data.mapIds[0]);
                    for (UUID uuid: uuids) {
                        if (!alreadySend.contains( uuid)){
                            MapPacket.sendPixelData(mapsData, uuid, pixelData);
                        }
                    }
                    alreadySend = uuids;


                } else {
                    // Send Packets
                    byte[][] pixelData = mapsData.getMapData();
                    List<MapPacket.MapPixelChecker> mapPixelCheckers = new ArrayList<>();
                    if (lastPixelData != null){
                        for (int i =0; i < mapsData.data.mapIds.length; i++) {
                            MapPacket.MapPixelChecker mpc = new MapPacket.MapPixelChecker( lastPixelData[i], pixelData[i], mapsData.data.mapIds[i]);
                            if (!mpc.notChange){
                                mapPixelCheckers.add(mpc);
                            }
                        }
                    }
                    List<UUID> uuids = getPacketNeeded( mapsData.data.mapIds[0]);
                    for (UUID uuid : uuids) {
                        if (alreadySend.contains(uuid)) {
                            MapPacket.sendPixelData(uuid, mapPixelCheckers);
                        } else {
                            MapPacket.sendPixelData(mapsData, uuid, pixelData);
                        }
                    }

                    lastPixelData = pixelData;
                    alreadySend = uuids;
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
