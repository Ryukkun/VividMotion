package fox.ryukkun_.vividmotion;

import fox.ryukkun_.vividmotion.screen.ScreenItemNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;

public class MapManager {
    private static Field worldMapF = null;
    private static Field colorF = null;
    static {
        try {
            Class<?> craftMapViewClass = MCVersion.craftBukkitClass("map.CraftMapView");
            worldMapF = craftMapViewClass.getDeclaredField("worldMap");
            worldMapF.setAccessible(true);

            Class<?> worldMapClass;
            if (MCVersion.isNewerThan(MCVersion.v1_17_R1)) {
                worldMapClass = Class.forName("net.minecraft.world.level.saveddata.maps.WorldMap");
            } else {
                worldMapClass = MCVersion.nmsClass("WorldMap");
            }
            colorF = Reflection.findField(worldMapClass, byte[].class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ScreenItemNBT getNewMapItemNBT(int mapId) {
        ScreenItemNBT nbtManager = new ScreenItemNBT(new ItemStack(Material.MAP));

        nbtManager.setMapId(mapId);
        nbtManager.setScreenItemId( ScreenItemNBT.ItemType.SCREEN_MAP);
        return nbtManager;
    }

    public static boolean isScreenMap(ItemStack itemStack) {
        return Byte.valueOf((byte)2).equals(ScreenItemNBT.getScreenItemId(itemStack));
    }


    public static void setColor(MapView mapView, byte[] pixelData) {
        try {
            colorF.set(worldMapF.get(mapView), pixelData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
