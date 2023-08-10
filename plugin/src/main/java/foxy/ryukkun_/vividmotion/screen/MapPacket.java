package foxy.ryukkun_.vividmotion.screen;

import foxy.ryukkun_.vividmotion.VividMotion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapPacket {
    public static void sendPixelData(UUID uuid, List<MapPixelChecker> MPCs) {
        // ! notChange
        Player player = Bukkit.getPlayer(uuid);
        if (player != null){

            List<fox.ryukkun_.MapPacket> packetList = new ArrayList<>();
            for (MapPixelChecker mpc : MPCs) {
                packetList.add(new fox.ryukkun_.MapPacket(mpc.mapId, mpc.pixelData));
            }

            VividMotion.packetManager.sendPacket(player, packetList);
        }
    }


    public static void sendPixelData(ScreenData sc, byte[][] pixelData){
        for (UUID uuid : VideoPlayer.getPacketNeeded(sc.data.mapIds[0])){
            sendPixelData(sc, uuid, pixelData);
        }
    }


    public static void sendPixelData(ScreenData sc, UUID uuid, byte[][] pixelData){
        Player player = Bukkit.getPlayer(uuid);
        if (player != null){

            List<fox.ryukkun_.MapPacket> packetList = new ArrayList<>();
            for (int i = 0; i < sc.data.mapIds.length; i++) {
                packetList.add(new fox.ryukkun_.MapPacket(sc.data.mapIds[i], pixelData[i]));
            }

            VividMotion.packetManager.sendPacket(player, packetList);
        }
    }


    public static class MapPixelChecker {
        public boolean fullChange = false;
        public boolean notChange = false;
        public int startX;
        public int startY;
        public int endX = 127;
        public int endY;
        public final byte[] pixelData;
        public final int mapId;

        public MapPixelChecker(byte[] oldByte, byte[] newByte, int mapId) {
            arrayCheck(oldByte, newByte);
            this.mapId = mapId;

            // set pixelData
            if (fullChange) pixelData = newByte;
            else if (notChange) pixelData = null;
            else pixelData = getPixelData(newByte);
        }


        private void arrayCheck(byte[] oldByte, byte[] NewByte) {
            int equalNum = 3;
            int startX = 125 - equalNum;
            int startY = 128;
            int endY = -1;

            for (int x = 0; x < equalNum; x++) {
                for (int y = 0; y < 128; y++) {
                    int i = y * 128 + 127 - x;
                    if (oldByte[i] != NewByte[i]) {
                        if (y < startY) startY = y;
                        if (endY < y) endY = y;
                    }
                }
                if (startY == 0 && endY == 127) {
                    break;
                } else if (endY == -1) {
                    notChange = true;
                    return;
                }
            }

            for (int y = startY; y <= endY; y++) {
                int breakCache = 0;
                for (int x = startX-1; 0 <= x; x--) {
                    int i = y * 128 + x;
                    if (oldByte[i] != NewByte[i]) {
                        startX = x;
                        breakCache = 0;
                    } else {
                        breakCache++;
                        if (equalNum <= breakCache) break;
                    }
                }
                if (startX == 0) break;
            }

            if (startX == 0 && startY == 0 && endY == 127) fullChange = true;
            this.startX = startX;
            this.startY = startY;
            this.endY = endY;
        }


        private byte[] getPixelData(byte[] rawPixelData){
            int xLength = endX+1-startX;
            int yLength = endY+1-startY;
            byte[] pixelData = new byte[xLength*yLength];

            int index = 0;
            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    int i = y * 128 + x;
                    pixelData[index++] = rawPixelData[i];
                }
            }
            return pixelData;
        }
    }
}
