package foxy.ryukkun_.vividmotion.event;

import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.commands.SetScreen;
import foxy.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class SetUpScreenItem implements Listener {
    @EventHandler
    public void onPlayerItemChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack is = player.getInventory().getItemInMainHand();
        if (!is.getType().equals(Material.ITEM_FRAME)){
            return;
        }

        String name = is.getItemMeta().getLore().get(0);
        for (ScreenData sd : VividMotion.screenDataList){
            if (sd.data.name.equals( name)){
                SetScreen.SetUpScreen.init_run(player, sd);
                return;
            }
        }
    }
}
