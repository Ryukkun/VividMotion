package fox.ryukkun_;

import net.minecraft.network.protocol.game.PacketPlayOutMap;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.saveddata.maps.MapIcon;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Packet_1_19_R3 implements PacketManager{
    @Override
    public void sendPacket(Player player, List<MapPacket> packets) {

        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        List<MapIcon> icon = new ArrayList<>();

        for (MapPacket packet : packets){
            PacketPlayOutMap NMSPacket = new PacketPlayOutMap(packet.mapId, (byte) 4, false, icon, new WorldMap.b(packet.sX, packet.sY, packet.fX, packet.fY, packet.color));
            connection.a(NMSPacket);
        }
    }
}
