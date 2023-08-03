package fox.ryukkun_;

import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.util.List;

public interface PacketManager {

    void sendPacket(Player player, List<MapPacket> packet);
}