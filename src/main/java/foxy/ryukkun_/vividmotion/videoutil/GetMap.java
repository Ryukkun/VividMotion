package foxy.ryukkun_.vividmotion.videoutil;

import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import net.minecraft.server.v1_12_R1.WorldMap;
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


    public WorldMap createMap(World world){
        net.minecraft.server.v1_12_R1.ItemStack stack = new net.minecraft.server.v1_12_R1.ItemStack(Items.MAP, 1, -1);
        return Items.FILLED_MAP.getSavedMap(stack, ((CraftWorld) world).getHandle());
    }

    private short getMapId(WorldMap map){
        return Short.parseShort(map.id.substring("map_".length()));
    }

    @Override
    public void run() {
        MapsData mData = new MapsData(ffs.width, ffs.height, ffs.frameRate);
        while (true){
            byte[] frame = ffs.read();
            if (frame == null){
                break;
            }
            mData.addFrame(frame);
        }
        mData.loaded();

        List<Short> item_count = new ArrayList<>();
        WorldMap view;
        byte[][] maps_pixel = mData.getMapDatas(0);

        for (byte[] bytes : maps_pixel) {
            view = this.createMap(player.getWorld());

            item_count.add( getMapId(view));

            view.colors = bytes;
            view.centerX = Integer.MAX_VALUE;
            view.centerZ = Integer.MAX_VALUE;
            view.scale = 0;
            view.mapView.getRenderers().clear();
            view.mapView.addRenderer(new MapRenderPicture( view));
        }

        int ii;
        NBTTagCompound nbt;
        NBTTagList tagList = new NBTTagList();
        List<NBTTagList> chestList = new ArrayList<>();
        for (int i = 0; i < item_count.size(); i++){
            ii = i % 27;

            nbt = new NBTTagCompound();
            nbt.setByte("Slot", (byte)ii);
            nbt.setByte("Count", (byte)1);
            nbt.setString("id", "minecraft:filled_map");
            nbt.setShort("Damage", item_count.get(i));

            tagList.add(nbt);

            if (ii == 26){
                chestList.add(tagList);
                tagList = new NBTTagList();
            }
        }


        if (tagList.size() != 0){
            chestList.add(tagList);
        }

        int i = 1;
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
