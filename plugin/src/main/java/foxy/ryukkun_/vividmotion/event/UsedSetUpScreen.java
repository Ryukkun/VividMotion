package foxy.ryukkun_.vividmotion.event;

import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.commands.SetScreen;
import foxy.ryukkun_.vividmotion.screen.ScreenData;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class UsedSetUpScreen implements Listener {
    @EventHandler
    public void onHangingPlaceEvent(HangingPlaceEvent event){
        // is set up Item?
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if (!SetScreen.SetUpScreen.isSetUpScreenItem(itemStack)) {
            event.setCancelled(true);
            return;
        }
        // is good direction?
        Player player = event.getPlayer();
        SetScreen.Direction face = new SetScreen.Direction(player);
        if (!SetScreen.SetUpScreen.isOver_1_13 && face.down || face.up) {
            event.setCancelled(true);
            return;
        }
        // find screedData
        ScreenData screenData = VividMotion.getScreenData( itemStack.getItemMeta().getLore().get(0));
        if (screenData == null) {
            event.setCancelled(true);
            return;
        }
        if (!SetScreen.SetUpScreen.canPlace(screenData, face)) {
            event.setCancelled(true);
            return;
        }


        // set ItemFrame
        int mapHeight = screenData.data.mapHeight, mapWidth = screenData.data.mapWidth;
        Location l;
        BlockFace frameFace;
        ItemFrame itemFrameE;
        int i = -1;

        if (face.down){
            frameFace = BlockFace.DOWN;
        } else if (face.up) {
            frameFace = BlockFace.UP;
        } else if (face.direction == 'N') {
            frameFace = BlockFace.NORTH;
        } else if (face.direction == 'E') {
            frameFace = BlockFace.EAST;
        } else if (face.direction == 'W') {
            frameFace = BlockFace.WEST;
        } else if (face.direction == 'S') {
            frameFace = BlockFace.SOUTH;
        } else {
            event.setCancelled(true);
            return;
        }


        event.getEntity().remove();
        try {
            for (int y = 0; y < mapHeight; y++) {
                for (int x = 0; x < mapWidth; x++) {

                    int ry = (mapHeight - mapHeight / 2) - 1 - y, rx = x - mapWidth / 2;
                    i++;

                    l = SetScreen.SetUpScreen.calcPosition(face.targetBlock.getLocation(), rx, ry, -1, face);

                    itemFrameE = (ItemFrame) player.getWorld().spawnEntity(l, EntityType.ITEM_FRAME);
                    itemFrameE.setFacingDirection(frameFace);
                    //                if (face.up){
                    //                    itemFrameE.setRotation();
                    //                }

                    NBTEditor.NBTCompound nbt = NBTEditor.getNBTCompound(new ItemStack(Material.MAP));
                    if (NBTEditor.getMinecraftVersion().lessThanOrEqualTo(NBTEditor.MinecraftVersion.v1_12)) {
                        nbt.set((short) screenData.data.mapIds[i], "Damage");
                    } else {
                        nbt.set(screenData.data.mapIds[i], "tag", "map");
                    }
                    itemFrameE.setItem(NBTEditor.getItemFromTag(nbt));
                }
            }
        } catch (Exception e){
            Bukkit.getLogger().warning(e.getMessage());
            event.setCancelled(true);
        }
    }
}
