package fox.ryukkun_.vividmotion;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;

public class MapManager {
    private static Field worldMapF = null;
    private static Field colorF = null;
    static {
        try {
            Class<?> craftMapViewClass = Class.forName(MCVersion.getCB()+"map.CraftMapView");
            worldMapF = craftMapViewClass.getDeclaredField("worldMap");
            worldMapF.setAccessible(true);

            Class<?> worldMapClass;
            if (MCVersion.greaterThanEqual(MCVersion.v1_17_R1)) {
                worldMapClass = Class.forName("net.minecraft.world.level.saveddata.maps.WorldMap");
            } else {
                worldMapClass = Class.forName(MCVersion.getNMS()+"WorldMap");
            }
            colorF = Reflection.findField(worldMapClass, byte[].class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getMapId(ItemStack is) {
        if (MCVersion.lessThanEqual(MCVersion.v1_12_R1)) {
            return is.getDurability();
        } else {
            return NBT.itemStackToNBT( is).getCompound("tag").getInteger("map");
        }
    }


    public static ItemStack getItem(int mapId) {
        return NBT.itemStackFromNBT( getItemNBT(mapId));
    }


    public static ReadWriteNBT getItemNBT(int mapId) {
        ItemStack itemStack = new ItemStack(Material.MAP);
        ReadWriteNBT nbt;

        if (MCVersion.lessThanEqual(MCVersion.v1_12_R1)) {
            itemStack.setDurability((short) mapId);
            nbt = NBT.itemStackToNBT(itemStack);
        } else {
            nbt = NBT.itemStackToNBT(itemStack);
            nbt.getOrCreateCompound("tag").setInteger("map", mapId);
        }

        nbt.getOrCreateCompound("tag").getOrCreateCompound("VividMotion").setByte("Item", (byte)2);
        return nbt;
    }

    public static boolean isScreenMap(ItemStack itemStack) {
        return NBT.itemStackToNBT(itemStack).getOrCreateCompound("tag").getOrCreateCompound("VividMotion").getByte("Item") == (byte)2;
    }


    public static void setColor(MapView mapView, byte[] pixelData) {
        try {
            colorF.set(worldMapF.get(mapView), pixelData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
