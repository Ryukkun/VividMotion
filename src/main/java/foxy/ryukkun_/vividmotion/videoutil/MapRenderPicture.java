package foxy.ryukkun_.vividmotion.videoutil;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapRenderPicture extends MapRenderer {
    private final byte[] mapPixel;
    public MapRenderPicture(byte[] mapPixel){
        this.mapPixel = mapPixel;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        int index = -1;
        for (int y = 0; y < 128; y++){
            for (int x = 0; x < 128; x++){
                index++;

                mapCanvas.setPixel(x,y,mapPixel[index]);
                mapView.getRenderers().clear();
            }
        }
    }
}
