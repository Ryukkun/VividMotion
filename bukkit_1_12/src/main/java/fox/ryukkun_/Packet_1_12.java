package fox.ryukkun_;

import net.minecraft.server.v1_12_R1.MapIcon;
import net.minecraft.server.v1_12_R1.PacketPlayOutMap;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class Packet_1_12 implements PacketManager{
    @Override
    public void sendPacket(Player player, List<MapPacket> packets) {

        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;

        List<MapIcon> empty = new ArrayList<>();
        for (MapPacket packet : packets){
            PacketPlayOutMap NMSpacket = new PacketPlayOutMap(packet.mapId, (byte) 4, false, empty, packet.color, packet.sX, packet.sY, packet.fX, packet.fY);
            connection.sendPacket(NMSpacket);
        }
    }
}