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
                            mapsData.sendPixelData(uuid, pixelData);
                        }
                    }
                    alreadySend = uuids;


                } else {
                    // Send Packets
                    byte[][] pixelData = mapsData.getMapData();
                    List<Integer> skipList = new ArrayList<>();
                    if (lastPixelData != null){
                        for (int i =0; i < mapsData.data.mapIds.length; i++) {
                            if (checkArray(pixelData[i], lastPixelData[i])){
                                skipList.add(i);
                            }
                        }
                    }
                    List<UUID> uuids = getPacketNeeded( mapsData.data.mapIds[0]);
                    for (UUID uuid : uuids) {
                        if (alreadySend.contains(uuid)) {
                            mapsData.sendPixelData(uuid, pixelData, skipList);
                        } else {
                            mapsData.sendPixelData(uuid, pixelData);
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


    private boolean checkArray(byte[] b1, byte[] b2) {
        for (int i = 0; i < 128; i++) {
            int ii = i * 128 + 127;
            if (b1[ii] != b2[ii]) {
                return false;
            }
        }
        return true;
    }


    public static class MapDetector extends MapRenderer {

        @Override
        public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
            updateTime(mapView.getId(), player.getUniqueId());
        }
    }
}
