package fox.ryukkun_;

import net.minecraft.server.v1_16_R2.PacketPlayOutMap;
import net.minecraft.server.v1_16_R2.PlayerConnection;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Packet_1_16_R2 implements PacketManager{
    @Override
    public void sendPacket(Player player, List<MapPacket> packets) {

        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;

        for (MapPacket packet : packets){
            PacketPlayOutMap NMSpacket = new PacketPlayOutMap(packet.mapId, (byte) 4, false, false, new ArrayList<>(), packet.color, packet.sX, packet.sY, packet.fX, packet.fY);
            connection.sendPacket(NMSpacket);
        }
    }
}
