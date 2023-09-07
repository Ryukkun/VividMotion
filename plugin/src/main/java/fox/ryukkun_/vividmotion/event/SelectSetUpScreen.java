package fox.ryukkun_.vividmotion.event;

import fox.ryukkun_.vividmotion.VividMotion;
import fox.ryukkun_.vividmotion.commands.SetScreen;
import fox.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SelectSetUpScreen implements Listener {
    @EventHandler
    public void onPlayerItemChange(PlayerItemHeldEvent event) {

        new BukkitRunnable(){
            @Override
            public void run() {
                Player player = event.getPlayer();
                ItemStack is = player.getInventory().getItemInMainHand();
                if (!SetScreen.SetUpScreen.isSetUpScreenItem(is)) return;

                ScreenData sd = SetScreen.SetUpScreen.getScreenData(is);
                if (sd != null){
                    SetScreen.SetUpScreen.init_run(player, sd, false);
                }
            }
        }.runTaskLater(VividMotion.plugin, 1L);
    }
}
