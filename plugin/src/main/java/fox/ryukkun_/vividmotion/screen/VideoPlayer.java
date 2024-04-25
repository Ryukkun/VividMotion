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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VideoPlayer extends Thread{

    public final ScreenData mapsData;
    private final VideoPacket[][] packetsTrimCache;
    public static final HashMap<Integer, HashMap<UUID, Long>> lastTime = new HashMap<>();
    public static final List<UUID> showUpdatePlayer = new ArrayList<>();
    private static final int FULL_UPDATE_DURATION = 10;

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

        List<Player> players = new ArrayList<>();
        for (UUID uuid : showUpdatePlayer) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) players.add(player);
        }



        if (players.isEmpty()) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : players) {
                    for (Entity entity : player.getNearbyEntities(15.0D, 15.0D, 15.0D)) {
                        if (!(entity instanceof ItemFrame)) continue;

                        ItemFrame itemFrame = (ItemFrame) entity;
                        ItemStack is = itemFrame.getItem();
                        if (!is.getType().equals(Material.MAP)) continue;

                        int mapId = ScreenItemNBT.getMapId(is);

                        int i = -1;
                        for (int _i = 0, limit = mapIds.length; _i < limit; _i++) {
                            if (mapId == mapIds[_i]) {
                                i = _i;
                                break;
                            }
                        }
                        if (i == -1) continue;
                        VideoPacket vp = videoPackets[i];
                        if (vp.noChange) continue;

                        LocationUtil l = new LocationUtil(itemFrame.getLocation(), itemFrame.getRotation());
                        Location start = l.clone().addLocalCoordinate(1.0/128*(vp.sX-64), -1.0/128*(vp.sY-64), 0.1);
                        Location fin = l.clone().addLocalCoordinate(1.0/128*(vp.fX-64), -1.0/128*(vp.fY-64), 0.1);

                        ParticleManager.spawnSquare(start, fin, player, Particle.REDSTONE, 0, 255, 0);
                    }
                }
            }
        }.runTask(VividMotion.plugin);
    }



    @Override
    public void run() {
        // Send Packet Client
        long next = System.currentTimeMillis();
        List<UUID> alreadySend = new ArrayList<>();
        byte[][] lastPixelData = null;
        int fullUpdateTiming = (int) (mapsData.data.videoFrameRate*FULL_UPDATE_DURATION);
        int fullUpdateTime = 0;

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

                    if (difPackets[0] == null) {
                        if (lastPixelData != null) {
                            difPackets = VideoPacket.getDifference(lastPixelData, pixelData);
                            packetsTrimCache[(int) mapsData.data.nowFrame] = difPackets;
                        }
                        lastPixelData = pixelData;
                    }

                    //// showUpdates
                    showScreenUpdates(difPackets, mapsData.data.mapIds);


                    //// send Packet
                    List<UUID> uuids = getPacketNeeded( mapsData.data.mapIds[0]);
                    for (UUID uuid : uuids) {
                        if (alreadySend.contains(uuid) && fullUpdateTime < fullUpdateTiming) {
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
                            fullUpdateTime %= fullUpdateTiming;
                        }
                    }

                    alreadySend = uuids;
                }

                // sleep
                next += (long) (1000 / mapsData.getFrameRate());
                Thread.sleep(Math.max(0,  next - System.currentTimeMillis()));
                fullUpdateTime++;
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
