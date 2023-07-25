package foxy.ryukkun_.vividmotion.videoutil;

import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapRenderPicture extends MapRenderer {

    private final WorldMap worldMap;
    public MapRenderPicture(WorldMap worldMap){
        this.worldMap = worldMap;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
//        Bukkit.getLogger().info(worldMap.i.toString());
//        Bukkit.getLogger().info(worldMap.k.toString());
        Bukkit.getLogger().info(player.getName());
        worldMap.flagDirty(0,0);
        worldMap.flagDirty(127,127);
//        int index = -1;
//        for (int y = 0; y < 128; y++){
//            for (int x = 0; x < 128; x++){
//                index++;
//
//                mapCanvas.setPixel(x,y,worldMap.colors[index]);
//            }
//        }
    }
}
