package foxy.ryukkun_.vividmotion.videoutil;

import net.minecraft.server.v1_12_R1.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
        List<ItemStack> items = new ArrayList<>();
        MapView view;
        byte[][] maps_pixel = mData.getMapDatas(0);

        for (byte[] bytes : maps_pixel) {
            view = Bukkit.createMap(player.getWorld());
            view.setCenterX(Integer.MAX_VALUE);
            view.setCenterZ(Integer.MAX_VALUE);
            view.setScale(MapView.Scale.FARTHEST);

            item = new ItemStack(Material.MAP, 1);
            item.setDurability(  view.getId());
            items.add(item);

            view.getRenderers().clear();
            view.addRenderer(new MapRenderPicture(bytes));
        }

        player.getInventory().addItem(items.toArray(new ItemStack[0]));
        Bukkit.getLogger().info("Finished");
    }
}
