package foxy.ryukkun_.vividmotion.videoutil;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapRenderPicture extends MapRenderer {

    private final byte[] pixelData;
    private final List<UUID> updated = new ArrayList<>();
    public MapRenderPicture(byte[] pixelData){
        this.pixelData = pixelData;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (!updated.contains(player.getUniqueId())) {
            for (int x = 0; x < 128; x++){
                for (int y = 0; y < 128; y++){
                    mapCanvas.setPixel(x, y, pixelData[y*128 + x]);
                }
            }
            updated.add(player.getUniqueId());
        }
    }
}
