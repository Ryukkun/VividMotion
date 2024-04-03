package fox.ryukkun_.vividmotion.screen;

import fox.ryukkun_.MapPacket;
import fox.ryukkun_.vividmotion.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapPacketSender {
    public static void sendPixelData(UUID uuid, List<MapPacket> MPCs) {
        // ! notChange
        Player player = Bukkit.getPlayer(uuid);
        if (player != null){

            PacketSender.sendPacket(player, MPCs);
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
                packetList.add(new MapPacket(sc.data.mapIds[i], pixelData[i]));
            }

            PacketSender.sendPacket(player, packetList);
        }
    }
}
