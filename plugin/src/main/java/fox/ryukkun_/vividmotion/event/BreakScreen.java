package fox.ryukkun_.vividmotion.event;

import fox.ryukkun_.vividmotion.LocationUtil;
import fox.ryukkun_.vividmotion.MapManager;
import fox.ryukkun_.vividmotion.VividMotion;
import fox.ryukkun_.vividmotion.commands.SetScreen;
import fox.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakScreen implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (SetScreen.SetUpScreen.isSetUpScreenItem( event.getPlayer().getInventory().getItemInMainHand())) {
            event.setCancelled(true);
        }
    }



    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!(damager instanceof Player) || !(entity instanceof ItemFrame)) return;

        ItemStack is = ((Player)damager).getInventory().getItemInMainHand();
        if (!SetScreen.SetUpScreen.isSetUpScreenItem( is)) return;

        ItemStack frameItem = ((ItemFrame)entity).getItem();
        if (!frameItem.getType().equals(Material.MAP)) return;


        int mapId = MapManager.getMapId(frameItem);
        int index = -1;
        event.setCancelled(true);
        ScreenData screenData = null;
        loop:
        for (ScreenData sd :VividMotion.screenDataList) {
            for (int i = 0, limit = sd.data.mapIds.length; i < limit; i++) {
                if (mapId == sd.data.mapIds[i]) {
                    screenData = sd;
                    index = i;
                    break loop;
                }
            }
        }

        if (screenData == null) return;
        LocationUtil base = new LocationUtil(entity.getLocation(), ((ItemFrame)entity).getRotation());
        base.addLocalCoordinate(-(index%screenData.data.mapWidth), index/screenData.data.mapWidth, 0);

        int nowIndex = 0;
        for (int y = 0; y < screenData.data.mapHeight; y++) {
            for (int x = 0; x < screenData.data.mapWidth; x++) {
                Location l = base.clone().addLocalCoordinate(x, -y, 0);
                int nowMapId = screenData.data.mapIds[nowIndex++];

                for (Entity nearEntity : l.getWorld().getNearbyEntities(l, 0.01, 0.01, 0.01)) {
                    if (!(nearEntity instanceof ItemFrame)) continue;

                    ItemStack item = ((ItemFrame)nearEntity).getItem();
                    if (!item.getType().equals(Material.MAP)) continue;
                    if (nowMapId != MapManager.getMapId(item)) continue;

                    nearEntity.remove();
                    break;
                }
            }
        }
    }
}
