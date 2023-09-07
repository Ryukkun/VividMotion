package fox.ryukkun_.vividmotion.event;

import fox.ryukkun_.vividmotion.LocationUtil;
import fox.ryukkun_.vividmotion.MCLogger;
import fox.ryukkun_.vividmotion.MapManager;
import fox.ryukkun_.vividmotion.commands.SetScreen;
import fox.ryukkun_.vividmotion.screen.ScreenData;
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

import java.util.ArrayList;
import java.util.List;

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
        ScreenData screenData = SetScreen.SetUpScreen.getScreenData(itemStack);
        if (screenData == null) {
            return;
        }

        // is good direction?
        Player player = event.getPlayer();
        SetScreen.Direction face = new SetScreen.Direction(player);
        if (!SetScreen.SetUpScreen.isOver_1_13 && !face.pitch.equals(0.0F) || face.pitch == null) {
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

        if (face.pitch == 90.0F){
            frameFace = BlockFace.DOWN;
        } else if (face.pitch == -90.0F) {
            frameFace = BlockFace.UP;
        } else if (face.yaw == 180.0F) {
            frameFace = BlockFace.NORTH;
        } else if (face.yaw == -90F) {
            frameFace = BlockFace.EAST;
        } else if (face.yaw == 90F) {
            frameFace = BlockFace.WEST;
        } else if (face.yaw == 0F) {
            frameFace = BlockFace.SOUTH;
        } else {
            event.setCancelled(true);
            return;
        }

        Location l1 = face.targetBlock.getLocation().add(0.5, 0.5, 0.5);
        l1.setYaw( face.yaw);
        l1.setPitch( face.pitch);
        LocationUtil lu = new LocationUtil(l1, 0);
        event.getEntity().remove();
        List<ItemFrame> summonedItemFrame = new ArrayList<>();
        try {
            for (int y = 0; y < mapHeight; y++) {
                for (int x = 0; x < mapWidth; x++) {

                    int ry = (mapHeight - mapHeight / 2) - 1 - y, rx = x - mapWidth / 2;
                    i++;


                    l = lu.clone().addLocalCoordinate(rx, ry, 1);
                    l.setX(l.getBlockX());
                    l.setY(l.getBlockY());
                    l.setZ(l.getBlockZ());
                    itemFrameE = (ItemFrame) player.getWorld().spawnEntity(l, EntityType.ITEM_FRAME);
                    itemFrameE.setFacingDirection(frameFace, true);
                    summonedItemFrame.add(itemFrameE);
                    if (face.pitch == -90F){
                        if (face.yaw == -90F) itemFrameE.setRotation(Rotation.COUNTER_CLOCKWISE_45);
                        else if (face.yaw == 180F) itemFrameE.setRotation(Rotation.CLOCKWISE);
                        else if (face.yaw == 90F) itemFrameE.setRotation(Rotation.CLOCKWISE_45);

                    } else if (face.pitch == 90F){
                        if (face.yaw == 90F) itemFrameE.setRotation(Rotation.COUNTER_CLOCKWISE_45);
                        else if (face.yaw == 180F) itemFrameE.setRotation(Rotation.CLOCKWISE);
                        else if (face.yaw == -90F) itemFrameE.setRotation(Rotation.CLOCKWISE_45);
                    }

                    itemFrameE.setItem( MapManager.getItem(screenData.data.mapIds[i]));
                }
            }
        } catch (Exception e){
            MCLogger.sendMessage(player, MCLogger.Level.Error, e.getMessage());
            event.setCancelled(true);
            for (ItemFrame iF : summonedItemFrame) {
                iF.remove();
            }
        }
    }
}
