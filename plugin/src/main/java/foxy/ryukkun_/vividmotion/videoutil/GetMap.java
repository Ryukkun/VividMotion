package foxy.ryukkun_.vividmotion.videoutil;


import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetMap extends Thread{
    public FFmpegSource ffs;
    public Player player;

    public GetMap(FFmpegSource ffs, Player player){
        this.ffs = ffs;
        this.player = player;
    }




    @Override
    public void run() {
        MapsData mData = new MapsData(ffs, player.getWorld());

        int i;
        int ii;
        int iii = 1;
        NBTEditor.NBTCompound nbt;
        List<ItemStack> chestList = new ArrayList<>();
        NBTEditor.NBTCompound chest = null;

        for (i = 0; i < mData.data.mapIds.length; i++){
            ii = i % 27;
            if (ii == 0){
                if (chest != null) {
                    chestList.add(NBTEditor.getItemFromTag(chest));
                }
                ItemStack itemChest = new ItemStack( Material.CHEST);
                ItemMeta meta = itemChest.getItemMeta();
                meta.setDisplayName("Maps " + iii++);
                meta.setLore( Collections.singletonList("(+NBT)"));
                itemChest.setItemMeta(meta);

                chest = NBTEditor.getNBTCompound( itemChest);
            }

            nbt = NBTEditor.getEmptyNBTCompound();
            nbt.set((byte)ii, "Slot");
            nbt.set((byte)1, "Count");
            nbt.set("minecraft:filled_map", "id");
            nbt.set((short) mData.data.mapIds[i], "Damage");

            chest.set(nbt, "tag", "BlockEntityTag", "Items", NBTEditor.NEW_ELEMENT);
        }

        if (chest != null){
            chestList.add(NBTEditor.getItemFromTag( chest));
        }

        player.getInventory().addItem( chestList.toArray(new ItemStack[0]));
    }

}
