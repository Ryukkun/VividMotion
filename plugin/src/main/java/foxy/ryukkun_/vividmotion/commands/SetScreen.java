package foxy.ryukkun_.vividmotion.commands;


import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.screen.ScreenData;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SetScreen extends ScreenCommandTemplate {
    @Override
    public void onCommandInCache(Player player, ScreenData screenData) {
        SetUpScreen.init_run(player, screenData, true);
    }


    @Override
    public void onCommandNotInCache(Player player, ScreenData screenData) {
        SetUpScreen.init_run(player, screenData, true);
    }


    public static class SetUpScreen extends BukkitRunnable{

        private final Player player;
        private final ScreenData screenData;
        public static final HashMap<UUID, HashMap<String, Boolean>> running = new HashMap<>();
        private static final boolean isOver_1_13;
        private static final ItemStack itemFrame = new ItemStack(Material.ITEM_FRAME);
        static {
            isOver_1_13 = (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( NBTEditor.MinecraftVersion.v1_13));

            ItemMeta im = itemFrame.getItemMeta();
            im.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            im.setDisplayName("SetUpScreen");
            im.setLore(Arrays.asList("右クリック で設置", "いらなかったら 消したり捨てていいよ"));
            itemFrame.setItemMeta(im);
        }

        private SetUpScreen(Player player, ScreenData screenData, boolean giveItem){
            this.player = player;
            this.screenData = screenData;

            if (giveItem){
                ItemStack item = itemFrame.clone();
                ItemMeta im = item.getItemMeta();
                List<String> l = im.getLore();
                l.addAll(0, Arrays.asList( screenData.data.name, ""));
                im.setLore( l);
                item.setItemMeta(im);

                PlayerInventory inv = player.getInventory();
                if (inv.getItemInMainHand().getType().equals(Material.AIR)){
                    inv.setItemInMainHand(item);
                } else{
                    inv.addItem(item);
                }
            }
        }

        public static void init_run(Player player, ScreenData screenData, boolean giveItem){
            UUID uuid = player.getUniqueId();
            if (!running.containsKey(uuid)){
                running.put(uuid, new HashMap<>());
            }
            HashMap<String, Boolean> uuidHash = running.get(uuid);
            Boolean b = uuidHash.get(screenData.data.name);
            if (b == null || !b){
                uuidHash.put(screenData.data.name, true);
                new SetUpScreen(player, screenData, giveItem).runTaskTimer(VividMotion.plugin, 0L, 1L);
            }
        }


        private static Location calcPosition(Location l, double rx, double ry, double rz, Character direction, boolean up, boolean down){
            l = l.clone();

            if (direction == 'N'){
                if (up){
                    l.add(rx, rz, ry);
                } else if (down) {
                    l.add(rx, -rz, -ry);
                } else {
                    l.add(rx, ry, -rz);
                }

            } else if (direction == 'S') {
                if (up){
                    l.add(-rx, rz, -ry);
                } else if (down) {
                    l.add(-rx, -rz, ry);
                } else {
                    l.add(-rx, ry, rz);
                }

            } else if (direction == 'E') {
                if (up){
                    l.add(-ry, rz, rx);
                } else if (down) {
                    l.add(ry, -rz, rx);
                } else {
                    l.add(rz, ry, rx);
                }

            } else if (direction == 'W') {
                if (up) {
                    l.add(ry, rz, -rx);
                } else if (down) {
                    l.add(-ry, -rz, -rx);
                } else {
                    l.add(-rz, ry, -rx);
                }
            }
            return l;
        }

        private static boolean canPlace(ScreenData screenData, Location location, Character direction, boolean up, boolean down){
            int mapHeight = screenData.data.mapHeight, mapWidth = screenData.data.mapWidth;

            Location l, l1;
            for (int y = 0; y < mapHeight; y++){
                for (int x = 0; x < mapWidth; x++){

                    int ry = y - mapHeight/2, rx = x - mapWidth/2;

                    l = calcPosition(location, rx, ry, 0.0, direction, up, down);
                    l1 = calcPosition(l, 0.0, 0.0, -1.0, direction, up, down);

                    if (l.getBlock().isEmpty() || l.getBlock().isLiquid() || !l1.getBlock().isEmpty()){
                        return false;
                    }

                }
            }
            return true;
        }


        public static Block getTargetBlock(Player p){
            if (p.getGameMode().equals(GameMode.SURVIVAL)){
                return p.getTargetBlock(null, 4);

            } else if (p.getGameMode().equals(GameMode.CREATIVE)) {
                return p.getTargetBlock(null, 5);

            }

            return null;
        }


        public static Character getDirection(Player p){
            double rotation = (p.getLocation().getYaw() - 90.0F) % 360.0F;

            if (rotation < 0.0D) {
                rotation += 360.0D;
            }
            if ((0.0D <= rotation) && (rotation < 45.0D))
                return 'W';
            if ((45.0D <= rotation) && (rotation < 135.0D))
                return 'N';
            if ((135.0D <= rotation) && (rotation < 225.0D))
                return 'E';
            if ((225.0D <= rotation) && (rotation < 315.0D))
                return 'S';
            if ((315.0D <= rotation) && (rotation < 360.0D)) {
                return 'W';
            }
            return null;
        }

        private void _cancel(){
            running.get(player.getUniqueId()).put(screenData.data.name, false);
            cancel();
        }

        @Override
        public void run() {
            ItemStack is = player.getInventory().getItemInMainHand();
            ItemMeta im = is.getItemMeta();

            if (im == null){
                _cancel();
                return;
            }
            if (!im.hasDisplayName() || !im.hasLore() || !is.getType().equals( Material.ITEM_FRAME)){
                _cancel();
            } else if (!im.getDisplayName().equals( itemFrame.getItemMeta().getDisplayName()) || !im.getLore().get(0).equals( screenData.data.name)) {
                _cancel();
            }

            Block b = getTargetBlock(player);
            Character direction = getDirection(player);
            if (b == null){
                return;
            }
            if (b.isEmpty() || b.isLiquid() || direction == null) {
                return;
            }

            boolean up = false, down = false;
            if (isOver_1_13) {
                float pitch = player.getLocation().getPitch();
                if (45.0 < pitch) {
                    down = true;
                } else if (pitch < -45.0) {
                    up = true;
                }
            }


            int mapHeight = screenData.data.mapHeight, mapWidth = screenData.data.mapWidth;
            boolean canPlace = canPlace(screenData, b.getLocation(), direction, up, down);

            List<Location> locations = new ArrayList<>();
            double _y = -(double)(mapHeight / 2), _x = -(double)(mapWidth / 2);
            double y = mapHeight + _y - 1, x = mapWidth + _x - 1;
            _y -= 0.4;
            _x -= 0.4;
            y += 0.4;
            x += 0.4;
            Location l = b.getLocation();
            l.setX(b.getX() + 0.5);
            l.setY(b.getY() + 0.5);
            l.setZ(b.getZ() + 0.5);
            l = calcPosition(l, 0.0, 0.0, -0.6, direction, up, down);

            Location l_x_y = calcPosition(l, _x, _y, 0, direction, up, down),
                    lxy = calcPosition(l, x, y, 0, direction, up, down);

            locations.add(lxy);
            locations.add(l_x_y);
            double xd = Math.abs(lxy.getX() - l_x_y.getX());
            double yd = Math.abs(lxy.getY() - l_x_y.getY());
            double zd = Math.abs(lxy.getZ() - l_x_y.getZ());
            double vecS = 0.3;
            Location lCopy, lCopy2;
            if ( 0.5 < xd){
                lCopy = lxy.clone();
                lCopy2 = l_x_y.clone();
                double v = (lxy.getX() - l_x_y.getX()) < 0 ? vecS : -vecS;
                for (double d = 0.0; d < xd; d+=vecS) {
                    lCopy.add(v, 0, 0);
                    lCopy2.add(-v, 0, 0);

                    locations.add(lCopy.clone());
                    locations.add(lCopy2.clone());
                }
            }
            if ( 0.5 < yd){
                lCopy = lxy.clone();
                lCopy2 = l_x_y.clone();
                double v = (lxy.getY() - l_x_y.getY()) < 0 ? vecS : -vecS;
                for (double d = 0.0; d < yd; d+=vecS) {
                    lCopy.add(0, v, 0);
                    lCopy2.add(0, -v, 0);

                    locations.add(lCopy.clone());
                    locations.add(lCopy2.clone());
                }
            }
            if ( 0.5 < zd){
                lCopy = lxy.clone();
                lCopy2 = l_x_y.clone();
                double v = (lxy.getZ() - l_x_y.getZ()) < 0 ? vecS : -vecS;
                for (double d = 0.0; d < zd; d+=vecS) {
                    lCopy.add(0, 0, v);
                    lCopy2.add(0, 0, -v);

                    locations.add(lCopy.clone());
                    locations.add(lCopy2.clone());
                }
            }

            for (Location ll : locations){
                if (canPlace){
                    player.getWorld().spawnParticle(Particle.REDSTONE, ll, 0, 0.01, 1, 0);
                }else{
                    player.getWorld().spawnParticle(Particle.REDSTONE, ll, 0, 0, 0, 0);
                }

            }




        }
    }
}
