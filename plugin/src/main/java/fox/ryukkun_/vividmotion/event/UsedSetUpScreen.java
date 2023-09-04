package fox.ryukkun_.vividmotion.event;

import fox.ryukkun_.vividmotion.MapManager;
import fox.ryukkun_.vividmotion.VividMotion;
import fox.ryukkun_.vividmotion.commands.SetScreen;
import fox.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
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
        if (!event.getEntity().getType().equals( EntityType.ITEM_FRAME) || !itemStack.getType().equals( Material.ITEM_FRAME)){
            return;
        }

        if (!SetScreen.SetUpScreen.isSetUpScreenItem(itemStack)) {
            return;
        }
        // find screen Data
        ScreenData screenData = VividMotion.getScreenData( itemStack.getItemMeta().getLore().get(0));
        if (screenData == null) {
            return;
        }

        // is good direction?
        Player player = event.getPlayer();
        SetScreen.Direction face = new SetScreen.Direction(player);
        if (!SetScreen.SetUpScreen.isOver_1_13 && (face.down || face.up) || !face.status) {
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
            frameFace = BlockFace.UP;
        } else if (face.up) {
            frameFace = BlockFace.DOWN;
        } else if (face.direction == 'N') {
            frameFace = BlockFace.SOUTH;
        } else if (face.direction == 'E') {
            frameFace = BlockFace.WEST;
        } else if (face.direction == 'W') {
            frameFace = BlockFace.EAST;
        } else if (face.direction == 'S') {
            frameFace = BlockFace.NORTH;
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
                    itemFrameE.setFacingDirection(frameFace, true);
                    if (face.up){
                        if (face.direction == 'E') itemFrameE.setRotation(Rotation.COUNTER_CLOCKWISE_45);
                        else if (face.direction == 'S') itemFrameE.setRotation(Rotation.CLOCKWISE);
                        else if (face.direction == 'W') itemFrameE.setRotation(Rotation.CLOCKWISE_45);

                    } else if (face.down){
                        if (face.direction == 'W') itemFrameE.setRotation(Rotation.COUNTER_CLOCKWISE_45);
                        else if (face.direction == 'S') itemFrameE.setRotation(Rotation.CLOCKWISE);
                        else if (face.direction == 'E') itemFrameE.setRotation(Rotation.CLOCKWISE_45);
                    }

                    itemFrameE.setItem( MapManager.getItem(screenData.data.mapIds[i]));
                }
            }
        } catch (Exception e){
            Bukkit.getLogger().warning(e.getMessage());
            event.setCancelled(true);
        }
    }
}
