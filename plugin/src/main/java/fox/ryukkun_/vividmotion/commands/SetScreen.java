package fox.ryukkun_.vividmotion.commands;


import fox.ryukkun_.ParticleUtil;
import fox.ryukkun_.ParticleUtil_1_12_R1;
import fox.ryukkun_.ParticleUtil_1_13_R1;
import fox.ryukkun_.vividmotion.VividMotion;
import fox.ryukkun_.vividmotion.screen.ScreenData;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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



    public static class Direction{
        public boolean up = false;
        public boolean down = false;
        public Character direction;
        public Block targetBlock = null;
        public boolean status = false;

        public Direction(Player player){
            List<Block> b = getTargetBlock(player);
            direction = getDirection(player);
            if (b == null) return;
            if (b.size() < 2) return;
            targetBlock = b.get(1);
            Block adjacentB = b.get(0);
            if (targetBlock.isEmpty() || targetBlock.isLiquid() || direction == null) {
                return;
            }

            up = false;
            down = false;
            BlockFace bf = targetBlock.getFace(adjacentB);
            if(bf.equals(BlockFace.UP)){
                down = true;
            } else if (bf.equals(BlockFace.DOWN)) {
                up = true;
            } else if (bf.equals(BlockFace.NORTH)) {
                direction = 'S';
            } else if (bf.equals(BlockFace.SOUTH)) {
                direction = 'N';
            } else if (bf.equals(BlockFace.WEST)) {
                direction = 'E';
            } else if (bf.equals(BlockFace.EAST)) {
                direction = 'W';
            }
            status = true;
        }

        public static List<Block> getTargetBlock(Player p){
            if (p.getGameMode().equals(GameMode.SURVIVAL)){
                return p.getLastTwoTargetBlocks(null, 4);

            } else if (p.getGameMode().equals(GameMode.CREATIVE)) {
                return p.getLastTwoTargetBlocks(null, 5);

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
    }



    public static class SetUpScreen extends BukkitRunnable{

        private final Player player;
        private final ScreenData screenData;
        public static final HashMap<UUID, HashMap<String, Boolean>> running = new HashMap<>();
        public static final boolean isOver_1_13 = (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( NBTEditor.MinecraftVersion.v1_13));

        public static final ParticleUtil particleUtil;
        static {
            if (isOver_1_13){
                particleUtil = new ParticleUtil_1_13_R1();
            } else{
                particleUtil = new ParticleUtil_1_12_R1();
            }
        }
        private static final ItemStack itemFrame = new ItemStack(Material.ITEM_FRAME);
        static {
            ItemMeta im = itemFrame.getItemMeta();
            im.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            im.setDisplayName("ScreenSetter");
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


        public static Location calcPosition(Location l, double rx, double ry, double rz, Direction face){
            l = l.clone();

            if (face.direction == 'N'){
                if (face.up){
                    l.add(rx, rz, ry);
                } else if (face.down) {
                    l.add(rx, -rz, -ry);
                } else {
                    l.add(rx, ry, -rz);
                }

            } else if (face.direction == 'S') {
                if (face.up){
                    l.add(-rx, rz, -ry);
                } else if (face.down) {
                    l.add(-rx, -rz, ry);
                } else {
                    l.add(-rx, ry, rz);
                }

            } else if (face.direction == 'E') {
                if (face.up){
                    l.add(-ry, rz, rx);
                } else if (face.down) {
                    l.add(ry, -rz, rx);
                } else {
                    l.add(rz, ry, rx);
                }

            } else if (face.direction == 'W') {
                if (face.up) {
                    l.add(ry, rz, -rx);
                } else if (face.down) {
                    l.add(-ry, -rz, -rx);
                } else {
                    l.add(-rz, ry, -rx);
                }
            }
            return l;
        }

        public static boolean canPlace(ScreenData screenData, Direction face){
            int mapHeight = screenData.data.mapHeight, mapWidth = screenData.data.mapWidth;

            Location l, l1;
            for (int y = 0; y < mapHeight; y++){
                for (int x = 0; x < mapWidth; x++){

                    int ry = y - mapHeight/2, rx = x - mapWidth/2;

                    l = calcPosition(face.targetBlock.getLocation(), rx, ry, 0.0, face);
                    l1 = calcPosition(l, 0.0, 0.0, -1.0, face);

                    if (l.getBlock().isEmpty() || l.getBlock().isLiquid() || !l1.getBlock().isEmpty()){
                        return false;
                    }
                }
            }
            return true;
        }


        public static boolean isSetUpScreenItem(ItemStack itemStack){
            ItemMeta im = itemStack.getItemMeta();

            if (im == null){
                return false;
            }
            if (!im.hasDisplayName() || !im.hasLore() || !itemStack.getType().equals( Material.ITEM_FRAME)){
                return false;
            } else return (im.getDisplayName().equals(itemFrame.getItemMeta().getDisplayName())) && (VividMotion.getScreenData( im.getLore().get(0)) != null);
        }


        private void _cancel(){
            running.get(player.getUniqueId()).put(screenData.data.name, false);
            cancel();
        }

        @Override
        public void run() {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if ( !isSetUpScreenItem( itemStack) || !player.isOnline()){
                _cancel();
                return;
            } else if (!itemStack.getItemMeta().getLore().get(0).equals( screenData.data.name)) {
                _cancel();
                return;
            }


            Direction face = new Direction(player);
            if (!isOver_1_13 && (face.down || face.up) || !face.status) {
                return;
            }


            int mapHeight = screenData.data.mapHeight, mapWidth = screenData.data.mapWidth;
            List<Location> locations = new ArrayList<>();
            double _y = -(double)(mapHeight / 2), _x = -(double)(mapWidth / 2);
            double y = mapHeight + _y - 1, x = mapWidth + _x - 1;
            _y -= 0.4;
            _x -= 0.4;
            y += 0.4;
            x += 0.4;
            Location l = face.targetBlock.getLocation();
            l.setX(face.targetBlock.getX() + 0.5);
            l.setY(face.targetBlock.getY() + 0.5);
            l.setZ(face.targetBlock.getZ() + 0.5);
            l = calcPosition(l, 0.0, 0.0, -0.6, face);

            Location l_x_y = calcPosition(l, _x, _y, 0, face),
                    lxy = calcPosition(l, x, y, 0, face);

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

            boolean canPlace = canPlace(screenData, face);
            for (Location ll : locations){
                if (canPlace){
                    particleUtil.spawnParticle(player.getWorld(), ll, 0, 255, 0);
                }else{
                    particleUtil.spawnParticle(player.getWorld(), ll, 255, 0, 0);
                }

            }
        }
    }
}
