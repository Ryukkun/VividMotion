package foxy.ryukkun_.vividmotion.commands;


import foxy.ryukkun_.vividmotion.screen.ScreenData;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.Arrays;

public class SetScreen extends ScreenCommandTemplate {
    @Override
    public void onCommandInCache(Player player, ScreenData screenData) {

    }


    @Override
    public void onCommandNotInCache(Player player, String name, String input) {

    }


    public static class SetUpScreen extends BukkitRunnable{

        private Player player;
        private ScreenData screenData;
        private static final ItemStack itemFrame = new ItemStack(Material.ITEM_FRAME);
        static {
            ItemMeta im = itemFrame.getItemMeta();
            im.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            im.setDisplayName("SetUpScreen");
            im.setLore(Arrays.asList("右クリック で設置", "いらなかったら 消したり捨てていいよ"));
            itemFrame.setItemMeta(im);
        }

        public void ScreenParticle(Player player, ScreenData screenData){
            this.player = player;
            this.screenData = screenData;
            PlayerInventory inv = player.getInventory();
            if (inv.getItemInMainHand().getType().equals(Material.AIR)){
                inv.setItemInMainHand(itemFrame);
            } else{
                inv.addItem(itemFrame);
            }
        }


        public static Block canPlace(Player p) {
            if (p.getGameMode().equals(GameMode.SURVIVAL)){
                return _canPlace(p, 5);

            } else if (p.getGameMode().equals(GameMode.CREATIVE)) {
                return _canPlace(p, 3);

            }

            return null;
        }

        private static Block _canPlace(Player p, int distance){
            p.getLocation().
            Block b = p.getTargetBlock(null, distance);
            b.
        }


        @Override
        public void run() {
            if (!player.getInventory().contains(itemFrame)){
                cancel();
            }


        }
    }
}
