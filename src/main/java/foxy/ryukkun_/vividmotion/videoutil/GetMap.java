package foxy.ryukkun_.vividmotion.videoutil;

import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.map.MapView;
import foxy.ryukkun_.vividmotion.videoutil.MapRenderPicture;

import java.util.ArrayList;
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

        ItemStack item;
        List<Short> item_count = new ArrayList<>();
        WorldMap view;
        byte[][] maps_pixel = mData.getMapDatas(0);

        for (byte[] bytes : maps_pixel) {
            view = this.createMap(player.getWorld());
//            view.setCenterX(Integer.MAX_VALUE);
//            view.setCenterZ(Integer.MAX_VALUE);
//            view.setScale(MapView.Scale.FARTHEST);

            item_count.add( getMapId(view));

            view.colors = bytes;
            view.centerX = Short.MAX_VALUE;
            view.centerZ = Short.MAX_VALUE;
            view.scale = 0;
            view.flagDirty(0,0);
            view.flagDirty(127, 127);
//            view.getRenderers().clear();
//            view.addRenderer(new MapRenderPicture(bytes));
        }

        int ii;
        net.minecraft.server.v1_12_R1.ItemStack nmsChest;
        NBTTagCompound nbt2, nbt;
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < item_count.size(); i++){
            ii = i % 27;

            nbt = new NBTTagCompound();
            nbt.setByte("Slot", (byte)ii);
            nbt.setByte("Count", (byte)1);
            nbt.setString("id", "minecraft:filled_map");
            nbt.setShort("Damage", item_count.get(i));

            tagList.add(nbt);

            if (ii == 26){
                give_chest(tagList);
                tagList = new NBTTagList();
            }
        }

        give_chest(tagList);
        Bukkit.getLogger().info("Finished");
    }

    private void give_chest(NBTTagList list){
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound nbt2 = new NBTTagCompound();
        nbt.set("Items", list);
        nbt2.set("BlockEntityTag", nbt);
        net.minecraft.server.v1_12_R1.ItemStack Chest = CraftItemStack.asNMSCopy( new ItemStack(Material.CHEST, 1));
        Chest.setTag(nbt2);

        Bukkit.getLogger().info( nbt2.toString());
        Bukkit.getLogger().info( Chest.toString());

        player.getInventory().addItem( CraftItemStack.asBukkitCopy( Chest));

    }
}
