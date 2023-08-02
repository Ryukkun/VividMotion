package foxy.ryukkun_.vividmotion.Packet;

import net.minecraft.server.v1_15_R1.PacketPlayOutMap;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Packet_1_15 implements PacketManager{
    @Override
    public void sendPacket(Player player, MapPacket... packets) {

        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;

        for (MapPacket packet : packets){
            PacketPlayOutMap NMSpacket = new PacketPlayOutMap(packet.mapId, (byte) 0, false, new ArrayList<>(), packet.color, packet.sX, packet.sY, packet.fX, packet.fY);
            connection.sendPacket(NMSpacket);
        }
    }
}
