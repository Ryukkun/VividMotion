package foxy.ryukkun_.vividmotion;

import net.minecraft.server.v1_12_R1.PersistentCollection;
import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.map.CraftMapView;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;

public class MapUtils {


    public static WorldMap getMap(int id, World world){
        PersistentCollection collection = ((CraftWorld)world).getHandle().worldMaps;
        return (WorldMap) collection.get(WorldMap.class, "map_" + id);
    }


    public static MapView createMap(World world){
        MapView view = Bukkit.createMap(world);

        if (view != null) {
            view.setCenterX(Integer.MAX_VALUE);
            view.setCenterZ(Integer.MAX_VALUE);
            view.setScale(MapView.Scale.FARTHEST);
            view.getRenderers().clear();
        }
        return view;
    }



    public static void setColor(MapView view, byte[] pixelData){
        try {
            CraftMapView cView = (CraftMapView) view;
            Field f = cView.getClass().getDeclaredField("worldMap");
            f.setAccessible(true);
            ((WorldMap) f.get(cView)).colors = pixelData;

        } catch (Exception e){
            Bukkit.getLogger().warning(e.toString());
        }
    }


    public static short getMapId(WorldMap map){
        return Short.parseShort(map.id.substring("map_".length()));
    }
}
