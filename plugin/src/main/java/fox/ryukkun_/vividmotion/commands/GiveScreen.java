package fox.ryukkun_.vividmotion.commands;


import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBTCompoundList;
import fox.ryukkun_.vividmotion.MapManager;
import fox.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiveScreen extends ScreenCommandTPL {

    private ItemStack[] convertToChest(int[] mapIds){
        int ii;
        int iii = 1;
        ReadWriteNBT chest = null;
        ReadWriteNBTCompoundList chestItems = null;
        ReadWriteNBT nbt;
        List<ItemStack> chestList = new ArrayList<>();

        for (int i = 0; i < mapIds.length; i++){
            ii = i % 27;
            if (ii == 0){
                ItemStack itemChest = new ItemStack(Material.CHEST);
                ItemMeta meta = itemChest.getItemMeta();
                meta.setDisplayName("Maps " + iii++);
                meta.setLore( Collections.singletonList("(+NBT)"));
                meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemChest.setItemMeta(meta);
                chest = NBT.itemStackToNBT(itemChest);
                chestItems = chest.getOrCreateCompound("tag")
                        .getOrCreateCompound("BlockEntityTag")
                        .getCompoundList("Items");
            }

            nbt = MapManager.getNewMapItemNBT(mapIds[i]).nbt;
            nbt.setByte("Slot", (byte) ii);
            nbt.setByte("Count", (byte) 1);


            chestItems.addCompound().mergeCompound(nbt);

            if (ii == 26 || i+1 == mapIds.length) {
                chestList.add(NBT.itemStackFromNBT( chest));
            }
        }

        return chestList.toArray(new ItemStack[0]);
    }

    private void addMapsInInventory(Player player, int[] mapIds){
        player.getInventory().addItem(convertToChest( mapIds));
    }


    @Override
    public void onCommandInCache(Player player, ScreenData screenData) {
        addMapsInInventory(player, screenData.data.mapIds);
    }

    @Override
    public void onCommandNotInCache(Player player, ScreenData screenData){
        addMapsInInventory(player, screenData.data.mapIds);
    }
}
