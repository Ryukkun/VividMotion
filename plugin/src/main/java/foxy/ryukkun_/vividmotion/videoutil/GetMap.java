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

        player.getInventory().addItem( convertToChest( mData.data.mapIds));
    }


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
}
