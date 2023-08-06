package foxy.ryukkun_.vividmotion.commands;


import foxy.ryukkun_.vividmotion.screen.ScreenData;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiveScreen extends ScreenCommandTemplate {

    private ItemStack[] convertToChest(int[] mapIds){
        int ii;
        int iii = 1;
        NBTEditor.NBTCompound chest = null;
        NBTEditor.NBTCompound nbt;
        List<ItemStack> chestList = new ArrayList<>();

        for (int i = 0; i < mapIds.length; i++){
            ii = i % 27;
            if (ii == 0){
                if (chest != null) {
                    chestList.add(NBTEditor.getItemFromTag(chest));
                }
                ItemStack itemChest = new ItemStack( Material.CHEST);
                ItemMeta meta = itemChest.getItemMeta();
                meta.setDisplayName("Maps " + iii++);
                meta.setLore( Collections.singletonList("(+NBT)"));
                meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemChest.setItemMeta(meta);

                chest = NBTEditor.getNBTCompound( itemChest);
            }

            nbt = NBTEditor.getEmptyNBTCompound();
            nbt.set((byte)ii, "Slot");
            nbt.set((byte)1, "Count");
            nbt.set("minecraft:filled_map", "id");
            if (NBTEditor.getMinecraftVersion().lessThanOrEqualTo(NBTEditor.MinecraftVersion.v1_12)){
                nbt.set((short) mapIds[i], "Damage");
            } else{
                nbt.set(mapIds[i], "tag", "map");
            }


            chest.set(nbt, "tag", "BlockEntityTag", "Items", NBTEditor.NEW_ELEMENT);
        }

        if (chest != null){
            chestList.add(NBTEditor.getItemFromTag( chest));
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
