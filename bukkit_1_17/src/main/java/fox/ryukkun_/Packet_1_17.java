package fox.ryukkun_;

import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Packet_1_17 implements PacketManager{
    @Override
    public void sendPacket(Player player, List<MapPacket> packets) {

        ServerPlayerConnection connection = ((CraftPlayer)player).getHandle().connection;

        for (MapPacket packet : packets){
            ClientboundMapItemDataPacket NMSpacket = new ClientboundMapItemDataPacket(packet.mapId, (byte) 0, false, new ArrayList<>(), new MapItemSavedData.MapPatch(packet.sX, packet.sY, packet.fX, packet.fY, packet.color));
            connection.send(NMSpacket);
        }
    }
}
