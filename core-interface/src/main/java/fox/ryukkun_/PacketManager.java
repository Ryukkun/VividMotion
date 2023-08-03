package fox.ryukkun_;

import org.bukkit.entity.Player;
import java.util.List;

public interface PacketManager {
    void sendPacket(Player player, List<MapPacket> packet);
}