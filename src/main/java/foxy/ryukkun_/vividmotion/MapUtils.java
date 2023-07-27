package foxy.ryukkun_.vividmotion;

import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.PersistentCollection;
import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

public class MapUtils {


    public static WorldMap getMap(int id, World world){
        PersistentCollection collection = ((CraftWorld)world).getHandle().worldMaps;
        return (WorldMap) collection.get(WorldMap.class, "map_" + id);
    }


    public static WorldMap createMap(World world){
        net.minecraft.server.v1_12_R1.ItemStack stack = new net.minecraft.server.v1_12_R1.ItemStack(Items.MAP, 1, -1);
        WorldMap worldMap = Items.FILLED_MAP.getSavedMap(stack, ((CraftWorld) world).getHandle());

        if (worldMap != null) {
            worldMap.centerX = Integer.MAX_VALUE;
            worldMap.centerZ = Integer.MAX_VALUE;
            worldMap.scale = 0;
            worldMap.mapView.getRenderers().clear();
        }
        return worldMap;
    }

    public static short getMapId(WorldMap map){
        return Short.parseShort(map.id.substring("map_".length()));
    }
}
