package foxy.ryukkun_.vividmotion.videoutil;

import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.PersistentCollection;
import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;

public class MpaUtils {


    public WorldMap getMap(int id, Player player){
        PersistentCollection collection = ((CraftWorld)player.getWorld()).getHandle().worldMaps;
        return (WorldMap) collection.get(WorldMap.class, "map_" + id);
    }


    public WorldMap createMap(Player player){
        net.minecraft.server.v1_12_R1.ItemStack stack = new net.minecraft.server.v1_12_R1.ItemStack(Items.MAP, 1, -1);
        return Items.FILLED_MAP.getSavedMap(stack, ((CraftWorld) player.getWorld()).getHandle());
    }

    private short getMapId(WorldMap map){
        return Short.parseShort(map.id.substring("map_".length()));
    }
}
