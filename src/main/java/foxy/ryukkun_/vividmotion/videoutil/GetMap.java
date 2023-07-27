package foxy.ryukkun_.vividmotion.videoutil;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
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

        int i, ii;
        NBTTagCompound nbt;
        NBTTagList tagList = new NBTTagList();
        List<NBTTagList> chestList = new ArrayList<>();
        for (i = 0; i < mData.data.mapIds.length; i++){
            ii = i % 27;

            nbt = new NBTTagCompound();
            nbt.setByte("Slot", (byte)ii);
            nbt.setByte("Count", (byte)1);
            nbt.setString("id", "minecraft:filled_map");
            nbt.setShort("Damage", (short) mData.data.mapIds[i]);

            tagList.add(nbt);

            if (ii == 26){
                chestList.add(tagList);
                tagList = new NBTTagList();
            }
        }


        if (!tagList.isEmpty()){
            chestList.add(tagList);
        }

        i = 1;
        ItemStack chest;
        for (NBTTagList maps :chestList){
            chest = giveChest(maps);
            ItemMeta meta = chest.getItemMeta();
            meta.setDisplayName("Maps " + i++);
            meta.setLore( Collections.singletonList("(+NBT)"));
            chest.setItemMeta(meta);

            player.getInventory().addItem( chest);
        }
    }

    private ItemStack giveChest(NBTTagList list) {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound nbt2 = new NBTTagCompound();

        nbt.set("Items", list);
        nbt2.set("BlockEntityTag", nbt);
        net.minecraft.server.v1_12_R1.ItemStack Chest = CraftItemStack.asNMSCopy( new ItemStack(Material.CHEST, 1));
        Chest.setTag(nbt2);

        return CraftItemStack.asBukkitCopy( Chest);
    }
}
