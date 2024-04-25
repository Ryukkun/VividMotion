package fox.ryukkun_.vividmotion.screen;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import fox.ryukkun_.vividmotion.MCVersion;
import org.bukkit.inventory.ItemStack;

public class ScreenItemNBT {
    public final ReadWriteNBT nbt;

    public ScreenItemNBT(ItemStack itemStack) {
        nbt = NBT.itemStackToNBT(itemStack);
    }

    public ItemStack toItemStack() {
        return NBT.itemStackFromNBT(nbt);
    }

    public void setScreenName(String name) {
        setScreenName(nbt, name);
    }
    public static void setScreenName(ReadWriteNBT nbt, String name) {
        if (MCVersion.greaterThanEqual(MCVersion.v1_20_R4)) {
            nbt.getOrCreateCompound("components")
                    .getOrCreateCompound("minecraft:custom_data")
                    .getOrCreateCompound("VividMotion")
                    .setString("ScreenName", name);

        } else {
            nbt.getOrCreateCompound("tag")
                    .getOrCreateCompound("VividMotion")
                    .setString("ScreenName", name);
        }
    }

    public String getScreenName() {
        return getScreenName(nbt);
    }
    public static String getScreenName(ItemStack itemStack) {
        return getScreenName(NBT.itemStackToNBT(itemStack));
    }
    public static String getScreenName(ReadWriteNBT nbt) {
        if (MCVersion.greaterThanEqual(MCVersion.v1_20_R4)) {
            return nbt.getOrCreateCompound("components")
                    .getOrCreateCompound("minecraft:custom_data")
                    .getCompound("VividMotion")
                    .getString("ScreenName");

        } else {
            return nbt.getOrCreateCompound("tag")
                    .getCompound("VividMotion")
                    .getString("ScreenName");
        }
    }


    public void setScreenItemId(byte id) {
        setScreenItemId(nbt, id);
    }
    public static void setScreenItemId(ReadWriteNBT nbt, byte id) {
        if (MCVersion.greaterThanEqual(MCVersion.v1_20_R4)) {
            nbt.getOrCreateCompound("components")
                    .getOrCreateCompound("minecraft:custom_data")
                    .getOrCreateCompound("VividMotion")
                    .setByte("Item", id);

        } else {
            nbt.getOrCreateCompound("tag")
                    .getOrCreateCompound("VividMotion")
                    .setByte("Item", id);
        }
    }


    public Byte getScreenItemId() {
        return getScreenItemId(nbt);
    }
    public static Byte getScreenItemId(ItemStack itemStack) {
        return getScreenItemId(NBT.itemStackToNBT(itemStack));
    }
    public static Byte getScreenItemId(ReadWriteNBT nbt) {
        if (MCVersion.greaterThanEqual(MCVersion.v1_20_R4)) {
            return nbt.getOrCreateCompound("components")
                    .getOrCreateCompound("minecraft:custom_data")
                    .getCompound("VividMotion")
                    .getByte("Item");

        } else {
            return nbt.getOrCreateCompound("tag")
                    .getCompound("VividMotion")
                    .getByte("Item");
        }
    }


    public void setMapId(int id) {
        setMapId(nbt, id);
    }
    public static void setMapId(ReadWriteNBT nbt, int id) {
        if (MCVersion.greaterThanEqual(MCVersion.v1_20_R4)) {
            nbt.getOrCreateCompound("components")
                    .setInteger("minecraft:map_id", id);

        } else if (MCVersion.greaterThanEqual(MCVersion.v1_13_R1)) {
            nbt.getOrCreateCompound("tag")
                    .setInteger("map", id);

        } else {
            nbt.setShort("Damage", (short) id);
        }
    }


    public Integer getMapId() {
        return getMapId(nbt);
    }
    public static Integer getMapId(ItemStack itemStack) {
        return getMapId(NBT.itemStackToNBT(itemStack));
    }
    public static Integer getMapId(ReadWriteNBT nbt) {
        if (MCVersion.greaterThanEqual(MCVersion.v1_20_R4)) {
            return nbt.getOrCreateCompound("components")
                    .getInteger("minecraft:map_id");

        } else if (MCVersion.greaterThanEqual(MCVersion.v1_13_R1)) {
            return nbt.getOrCreateCompound("tag")
                    .getInteger("map");

        } else {
            return (int) nbt.getShort("Damage");
        }
    }
}
