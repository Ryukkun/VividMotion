package fox.ryukkun_.vividmotion.commands;


import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import fox.ryukkun_.vividmotion.LocationUtil;
import fox.ryukkun_.vividmotion.MCVersion;
import fox.ryukkun_.vividmotion.ParticleManager;
import fox.ryukkun_.vividmotion.VividMotion;
import fox.ryukkun_.vividmotion.screen.ScreenItemNBT;
import fox.ryukkun_.vividmotion.screen.ScreenData;
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

public class SetScreen extends ScreenCommandTPL {
    @Override
    public void onCommandInCache(Player player, ScreenData screenData) {
        SetUpScreen.init_run(player, screenData, true);
    }


    @Override
    public void onCommandNotInCache(Player player, ScreenData screenData) {
        SetUpScreen.init_run(player, screenData, true);
    }



    public static class Direction{
        public Float yaw = null , pitch = null;
        public Block targetBlock;

        public Direction(Player player) {
            List<Block> b = getTargetBlock(player);
            if (b == null) return;
            if (b.size() < 2) return;
            targetBlock = b.get(1);
            Block adjacentB = b.get(0);
            if (targetBlock.isEmpty() || targetBlock.isLiquid()) {
                return;
            }

            pitch = 0.0F;
            BlockFace bf = targetBlock.getFace(adjacentB);
            if(bf.equals(BlockFace.UP)){
                pitch = -90.0F;
                yaw = getDirectionFloat(player);
            } else if (bf.equals(BlockFace.DOWN)) {
                pitch = 90.0F;
                yaw = getDirectionFloat(player);
            } else if (bf.equals(BlockFace.NORTH)) {
                yaw = 180F;
            } else if (bf.equals(BlockFace.SOUTH)) {
                yaw = 0F;
            } else if (bf.equals(BlockFace.WEST)) {
                yaw = 90F;
            } else if (bf.equals(BlockFace.EAST)) {
                yaw = -90F;
            }
        }


        public static List<Block> getTargetBlock(Player p){
            if (p.getGameMode().equals(GameMode.SURVIVAL)){
                return p.getLastTwoTargetBlocks(null, 4);

            } else if (p.getGameMode().equals(GameMode.CREATIVE)) {
                return p.getLastTwoTargetBlocks(null, 5);

            }
            return null;
        }



        public static float getDirectionFloat(Player p){
            float rotation = p.getLocation().getYaw() % 360.0F;

            if (rotation < 0.0F) {
                rotation += 360.0F;
            }
            if ((0.0F <= rotation) && (rotation < 45.0F))
                return 180.0F;
            if ((45.0F <= rotation) && (rotation < 135.0F))
                return -90.0F;
            if ((135.0F <= rotation) && (rotation < 225.0F))
                return 0.0F;
            if ((225.0F <= rotation) && (rotation < 315.0F))
                return 90.0F;
            if ((315.0F <= rotation) && (rotation < 360.0F)) {
                return 180.0F;
            }
            return 0.0F;
        }
    }




    public static class SetUpScreen extends BukkitRunnable{

        private final Player player;
        private final ScreenData screenData;
        public static final HashMap<UUID, HashMap<String, Boolean>> running = new HashMap<>();
        public static final boolean isOver_1_13 = (MCVersion.isNewerThan(MCVersion.v1_13_R1));

        private static final ItemStack itemFrame = getItemFrame();
        private static ItemStack getItemFrame() {
            ItemStack is = new ItemStack(Material.ITEM_FRAME);
            ItemMeta im = is.getItemMeta();
            im.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            im.setLore(Arrays.asList("右クリック で設置", "いらなかったら 消したり捨てていいよ"));
            is.setItemMeta(im);

            NBTItem nbtItem = new NBTItem(is);
            ReadWriteNBT nbt = nbtItem.getOrCreateCompound("VividMotion");
            nbt.setByte("Item", (byte)1);
            nbt.setString("ScreenName", "");

            return nbtItem.getItem();
        }

        private SetUpScreen(Player player, ScreenData screenData, boolean giveItem){
            this.player = player;
            this.screenData = screenData;

            if (giveItem){
                ItemStack item = itemFrame.clone();
                ItemMeta im = item.getItemMeta();
                im.setDisplayName(ChatColor.GRAY+"screen: "+ChatColor.WHITE+ChatColor.BOLD+screenData.data.name);
                item.setItemMeta(im);
                NBTItem nbt = new NBTItem(item);
                nbt.getOrCreateCompound("VividMotion").setString("ScreenName",screenData.data.name);
                item = nbt.getItem();

                //MCLogger.sendMessage(player, MCLogger.Level.Success, NBT.itemStackToNBT(item).toString());
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



        public static boolean canPlace(ScreenData screenData, Direction face){
            int mapHeight = screenData.data.mapHeight, mapWidth = screenData.data.mapWidth;

            Location l, l1;
            for (int y = 0; y < mapHeight; y++){
                for (int x = 0; x < mapWidth; x++){

                    int ry = y - mapHeight/2, rx = x - mapWidth/2;

                    l = face.targetBlock.getLocation().add(0.5, 0.5, 0.5);
                    l.setPitch(face.pitch);
                    l.setYaw(face.yaw);
                    LocationUtil lu = new LocationUtil(l, 0.0);
                    l = lu.addLocalCoordinate(rx, ry, 0.0);
                    l1 = lu.addLocalCoordinate(0.0, 0.0, 1.0);

                    if (l.getBlock().isEmpty() || l.getBlock().isLiquid() || !l1.getBlock().isEmpty()){
                        return false;
                    }
                }
            }
            return true;
        }


        public static boolean isSetUpScreenItem(ItemStack itemStack){
            if (!itemStack.getType().equals( Material.ITEM_FRAME)) return false;

            ReadWriteNBT nbt = new NBTItem(itemStack).getCompound("VividMotion");
            if (nbt == null) return false;
            return nbt.getByte("Item").equals((byte) 1);
        }


        public static ScreenData getScreenData(ItemStack itemStack) {
            return VividMotion.getScreenData(ScreenItemNBT.getScreenName(itemStack));
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
            } else if (!ScreenItemNBT.getScreenName(itemStack).equals( screenData.data.name)) {
                _cancel();
                return;
            }


            Direction face = new Direction(player);
            if (face.yaw == null || face.pitch == null) return;
            if (!isOver_1_13 && !face.pitch.equals(0.0F)) return;


            int mapHeight = screenData.data.mapHeight, mapWidth = screenData.data.mapWidth;
            double _y = -(double)(mapHeight / 2), _x = -(double)(mapWidth / 2);
            double y = mapHeight + _y - 1, x = mapWidth + _x - 1;
            _y -= 0.4;
            _x -= 0.4;
            y += 0.4;
            x += 0.4;
            Location l = face.targetBlock.getLocation().add(0.5, 0.5, 0.5);

            l.setYaw( face.yaw);
            l.setPitch( face.pitch);
            LocationUtil lu = new LocationUtil(l, 0.0);
            lu.addLocalCoordinate(0, 0, 0.6);

            Location l_x_y = lu.clone().addLocalCoordinate(_x, _y, 0),
                    lxy = lu.clone().addLocalCoordinate(x, y, 0);


            boolean canPlace = canPlace(screenData, face);
            if (canPlace){
                ParticleManager.spawnSquare(lxy, l_x_y, player, Particle.REDSTONE, 0, 255, 0);
            }else{
                ParticleManager.spawnSquare(lxy, l_x_y, player, Particle.REDSTONE, 255, 0, 0);
            }

        }
    }
}
