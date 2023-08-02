package foxy.ryukkun_.vividmotion.Packet;

import foxy.ryukkun_.vividmotion.Packet.MapPacket;
import foxy.ryukkun_.vividmotion.Packet.PacketManager;
import net.minecraft.server.v1_13_R2.PacketPlayOutMap;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Packet_1_13 implements PacketManager {
    @Override
    public void sendPacket(Player player, MapPacket... packets) {

        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;

        for (MapPacket packet : packets){
            PacketPlayOutMap NMSpacket = new PacketPlayOutMap(packet.mapId, (byte) 0, false, new ArrayList<>(), packet.color, packet.sX, packet.sY, packet.fX, packet.fY);
            connection.sendPacket(NMSpacket);
        }
    }
}
