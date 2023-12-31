package fox.ryukkun_;

import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Packet_1_19_R2 implements PacketManager{
    @Override
    public void sendPacket(Player player, List<MapPacket> packets) {

        ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;

        for (MapPacket packet : packets){
            ClientboundMapItemDataPacket NMSpacket = new ClientboundMapItemDataPacket(packet.mapId, (byte) 4, false, new ArrayList<>(), new MapItemSavedData.MapPatch(packet.sX, packet.sY, packet.fX, packet.fY, packet.color));
            connection.send(NMSpacket);
        }
    }
}
