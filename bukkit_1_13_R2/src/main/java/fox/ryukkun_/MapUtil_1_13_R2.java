package fox.ryukkun_;

import net.minecraft.server.v1_13_R2.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.map.CraftMapView;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;

public class MapUtil_1_13_R2 implements MapUtil, MapGetter{

    @Override
    public void setColor(MapView view, byte[] pixelData) {
        try {
            CraftMapView cView = (CraftMapView) view;
            Field f = cView.getClass().getDeclaredField("worldMap");
            f.setAccessible(true);
            ((WorldMap) f.get(cView)).colors = pixelData;

        } catch (Exception e){
            Bukkit.getLogger().warning(e.toString());
        }
    }

    @Override
    public MapView getMap(int id) {
        return Bukkit.getMap( id);
    }
}
