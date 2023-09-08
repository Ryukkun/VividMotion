package fox.ryukkun_.vividmotion.event;

import fox.ryukkun_.vividmotion.MapManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class InteractScreen implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!(damager instanceof Player) || !(entity instanceof ItemFrame)) return;

        if (cancelCheck((ItemFrame) entity, (Player) damager)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof ItemFrame)) return;

        if (cancelCheck((ItemFrame) entity, event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    private boolean cancelCheck(ItemFrame itemFrame, Player player) {
        ItemStack frameItem = itemFrame.getItem();
        if (!frameItem.getType().equals(Material.MAP) || player.getGameMode().equals(GameMode.CREATIVE)) return false;

        return MapManager.isScreenMap(frameItem);
    }
}
