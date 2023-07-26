package foxy.ryukkun_.vividmotion.videoutil;

import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapRenderPicture extends MapRenderer {

    private final WorldMap worldMap;
    private final List<UUID> updated = new ArrayList<>();
    public MapRenderPicture(WorldMap worldMap){
        this.worldMap = worldMap;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (updated.contains(player.getUniqueId())) {
            worldMap.flagDirty(0, 0);
            worldMap.flagDirty(127, 127);
            updated.add(player.getUniqueId());
        }
    }
}
