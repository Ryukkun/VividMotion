package fox.ryukkun_.vividmotion;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import fox.ryukkun_.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class MapManager {
    public static MapUtil mapUtil;
    static {
        if (MCVersion.equal( MCVersion.v1_12_R1)){
            mapUtil = new MapUtil_1_12_R1();
        } else if (MCVersion.equal( MCVersion.v1_13_R1)) {
            mapUtil = new MapUtil_1_13_R1();
        } else if (MCVersion.equal( MCVersion.v1_13_R2)) {
            mapUtil = new MapUtil_1_13_R2();
        } else if (MCVersion.equal( MCVersion.v1_14_R1)) {
            mapUtil = new MapUtil_1_14_R1();
        } else if (MCVersion.equal( MCVersion.v1_15_R1)) {
            mapUtil = new MapUtil_1_15_R1();
        } else if (MCVersion.equal( MCVersion.v1_16_R1)) {
            mapUtil = new MapUtil_1_16_R1();
        } else if (MCVersion.equal( MCVersion.v1_16_R2)) {
            mapUtil = new MapUtil_1_16_R2();
        } else if (MCVersion.equal( MCVersion.v1_16_R3)) {
            mapUtil = new MapUtil_1_16_R3();
        } else if (MCVersion.equal( MCVersion.v1_17_R1)) {
            mapUtil = new MapUtil_1_17_R1();
        } else if (MCVersion.equal( MCVersion.v1_18_R1)) {
            mapUtil = new MapUtil_1_18_R1();
        } else if (MCVersion.equal( MCVersion.v1_18_R2)) {
            mapUtil = new MapUtil_1_18_R2();
        } else if (MCVersion.equal( MCVersion.v1_19_R1)) {
            mapUtil = new MapUtil_1_19_R1();
        } else if (MCVersion.equal( MCVersion.v1_19_R2)) {
            mapUtil = new MapUtil_1_19_R2();
        } else if (MCVersion.equal( MCVersion.v1_19_R3)) {
            mapUtil = new MapUtil_1_19_R3();
        } else if (MCVersion.equal( MCVersion.v1_20_R1)){
            mapUtil = new MapUtil_1_20_R1();
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
        mapUtil.setColor(mapView, pixelData);
    }

}
