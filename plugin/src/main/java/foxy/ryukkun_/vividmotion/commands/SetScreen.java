package foxy.ryukkun_.vividmotion.commands;


import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.screen.ScreenData;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.*;

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

        private SetUpScreen(Player player, ScreenData screenData){
            this.player = player;
            this.screenData = screenData;

            ItemStack item = itemFrame;
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

        public static void init_run(Player player, ScreenData screenData){
            UUID uuid = player.getUniqueId();
            if (!running.containsKey(uuid)){
                running.put(uuid, new HashMap<>());
            }
            HashMap<String, Boolean> uuidHash = running.get(uuid);
            if (uuidHash.get(screenData.data.name)){
                return;
            } else{
                uuidHash.put(screenData.data.name, true);
                new SetUpScreen(player, screenData).runTaskTimer(VividMotion.plugin, 0L, 1L);
            }
        }


        private static Location calcPosition(Location l, double rx, double ry, double rz, Character direction, boolean up, boolean down){
            if (direction == 'N'){
                if (up){
                    l = l.add(rx, rz, ry);
                } else if (down) {
                    l = l.add(rx, -rz, -ry);
                } else {
                    l = l.add(rx, ry, -rz);
                }

            } else if (direction == 'S') {
                if (up){
                    l = l.add(-rx, rz, -ry);
                } else if (down) {
                    l = l.add(-rx, -rz, ry);
                } else {
                    l = l.add(-rx, ry, rz);
                }

            } else if (direction == 'E') {
                if (up){
                    l = l.add(-ry, rz, rx);
                } else if (down) {
                    l = l.add(ry, -rz, rx);
                } else {
                    l = l.add(rz, ry, rx);
                }

            } else if (direction == 'W') {
                if (up) {
                    l = l.add(ry, rz, -rx);
                } else if (down) {
                    l = l.add(-ry, -rz, -rx);
                } else {
                    l = l.add(-rz, ry, -rx);
                }
            }
            return l;
        }

        private static boolean canPlace(ScreenData screenData, Location location, Character direction, boolean up, boolean down){
            int mapHeight = screenData.data.mapHeight, mapWidth = screenData.data.mapWidth;


            for (int y = 0; y < mapHeight; y++){
                for (int x = 0; x < mapWidth; x++){

                    Location l = location;
                    Location l1 = l.clone();
                    int ry = y - mapHeight/2, rx = x - mapWidth/2;

                    l = calcPosition(l, rx, ry, 0.0, direction, up, down);
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
                return p.getTargetBlock(null, 5);

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


        @Override
        public void run() {
            if (!player.getInventory().contains(itemFrame)) {
                running.get(player.getUniqueId()).put(screenData.data.name, false);
                cancel();
            }

            Block b = getTargetBlock(player);
            Character direction = getDirection(player);
            if (b == null || direction == null) {
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
            y += 0.4;
            Location l = b.getLocation();
            l.setX(b.getX() + 0.5);
            l.setY(b.getY() + 0.5);
            l.setZ(b.getZ() + 0.5);
            l = calcPosition(l, 0.0, 0.0, -0.6, direction, up, down);

            Location l_x_y = calcPosition(l, _x, _y, 0, direction, up, down),
                    lxy = calcPosition(l, x, y, 0, direction, up, down);



            for () {

            }
            player.getWorld().spawnPar



        }
    }
}
