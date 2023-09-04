package fox.ryukkun_.vividmotion.screen;

import fox.ryukkun_.MapPacket;
import fox.ryukkun_.vividmotion.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.*;

public class VideoPlayer extends Thread{

    public final ScreenData mapsData;
    private final VideoPacket[][] packetsTrimCache;
    public static final HashMap<Integer, HashMap<UUID, Long>> lastTime = new HashMap<>();
    public static final List<UUID> showUpdatePlayer = new ArrayList<>();


    public VideoPlayer(ScreenData mapsData){
        this.mapsData = mapsData;
        packetsTrimCache = new VideoPacket[mapsData.data.frameCount][mapsData.data.mapIds.length];
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


    private static void showScreenUpdates(VideoPacket[] videoPackets, int[] mapIds) {
        for (UUID uuid : showUpdatePlayer) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            for (Entity entity : player.getNearbyEntities(20.0D, 20.0D, 20.0D)) {
                if (!(entity instanceof ItemFrame)) continue;

                ItemFrame itemFrame = (ItemFrame) entity;
                ItemStack is = itemFrame.getItem();
                if (!is.getType().equals(Material.MAP)) continue;

                int mapId = MapManager.getMapId(is);


                for (int i = 0, limit = mapIds.length; i < limit; i++) {
                    if (mapId == mapIds[i]) {
                        VideoPacket vp = videoPackets[i];
                        if (vp.noChange) continue;

                        LocationUtil l = new LocationUtil(itemFrame.getLocation(), itemFrame.getRotation());
                        Location start = l.clone().addLocalCoordinate(1.0/128*(vp.sX-64), 1.0/128*(vp.sY), 0.1);
                        Location fin = l.clone().addLocalCoordinate(1.0/128*(vp.fX-64), 1.0/128*(vp.fY), 0.1);

                        ParticleManager.spawnSquare(start, fin, player, Particle.CRIT);
                    }
                }
            }


        }
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
                    // is Pausing
                    // Send Packets
                    byte[][] pixelData = mapsData.getMapData( mapsData.data.nowFrame);
                    List<UUID> uuids = getPacketNeeded( mapsData.data.mapIds[0]);
                    for (UUID uuid: uuids) {
                        if (!alreadySend.contains( uuid)){
                            MapPacketSender.sendPixelData(mapsData, uuid, pixelData);
                        }
                    }
                    alreadySend = uuids;


                } else {
                    // is not Pausing
                    //// get Difference
                    byte[][] pixelData = mapsData.getMapData();
                    VideoPacket[] difPackets = packetsTrimCache[(int) mapsData.data.nowFrame];

                    if (difPackets[0] == null && lastPixelData != null) {
                        difPackets = VideoPacket.getDifference(lastPixelData, pixelData);
                        packetsTrimCache[(int) mapsData.data.nowFrame] = difPackets;
                    }

                    //// showUpdates
                    showScreenUpdates(difPackets, mapsData.data.mapIds);

                    //// send Packet
                    List<UUID> uuids = getPacketNeeded( mapsData.data.mapIds[0]);
                    for (UUID uuid : uuids) {
                        if (alreadySend.contains(uuid) && mapsData.data.nowFrame != 0) {
                            // 部分的に送信
                            List<MapPacket> packets = new ArrayList<>();
                            for (int i = 0; i < difPackets.length; i++) {
                                VideoPacket vp = difPackets[i];
                                if (vp.noChange) continue;

                                packets.add( vp.getMapPacket( mapsData.data.mapIds[i], pixelData[i]));
                            }
                            MapPacketSender.sendPixelData(uuid, packets);

                        } else {
                            // 全てのピクセル送信
                            MapPacketSender.sendPixelData(mapsData, uuid, pixelData);
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
