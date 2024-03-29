package fox.ryukkun_.vividmotion;

import fox.ryukkun_.MapPacket;
import fox.ryukkun_.PacketManager;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PacketSender implements PacketManager {
    private static Constructor<?> packetC = null;
    private static Class<?> craftPlayerClass = null;
    private static Method sendPacketMethod = null;
    private static Field playerConnectionF = null;
    private static Constructor<?> MapPatchC = null;

    static {
        try {
            if (MCVersion.greaterThanEqual(MCVersion.v1_17_R1)) {
                Class<?> c =Class.forName("net.minecraft.network.protocol.game.ClientboundMapItemDataPacket");
                sendPacketMethod = Class.forName("net.minecraft.server.network.ServerPlayerConnection").getMethod("send", c);
                playerConnectionF = Class.forName("net.minecraft.server.level.ServerPlayer").getField("connection");
                Class<?> MapPatchClass = Class.forName("net.minecraft.world.level.saveddata.maps.MapItemSavedData.MapPatch");
                MapPatchC = MapPatchClass.getConstructor(int.class, int.class, int.class, int.class, byte[].class);
                packetC = c.getConstructor(int.class, byte.class, boolean.class, Collection.class, MapPatchClass);
            } else if (MCVersion.greaterThanEqual(MCVersion.v1_14_R1)) {
                Class<?> c = Class.forName(MCVersion.getNMS()+"PacketPlayOutMap");
                packetC = c.getConstructor(int.class, byte.class, boolean.class, boolean.class, Collection.class, byte[].class, int.class, int.class, int.class, int.class);
                sendPacketMethod = Class.forName(MCVersion.getNMS()+"PlayerConnection").getMethod("sendPacket", c);
                playerConnectionF = Class.forName(MCVersion.getNMS()+"EntityPlayer").getField("playerConnection");
            } else {
                Class<?> c = Class.forName(MCVersion.getNMS()+"PacketPlayOutMap");
                packetC = c.getConstructor(int.class, byte.class, boolean.class, Collection.class, byte[].class, int.class, int.class, int.class, int.class);
                sendPacketMethod = Class.forName(MCVersion.getNMS()+"PlayerConnection").getMethod("sendPacket", c);
                playerConnectionF = Class.forName(MCVersion.getNMS()+"EntityPlayer").getField("playerConnection");
            }
            craftPlayerClass = Class.forName(MCVersion.getCB()+"CraftPlayer");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPacket(Player player, List<MapPacket> packets) {
        try {
            Object connection = playerConnectionF.get(craftPlayerClass.getMethod("getHandle").invoke(player));

            PacketSender.packetC.newInstance();
            if (MCVersion.greaterThanEqual(MCVersion.v1_17_R1)) {
                for (MapPacket packet : packets){
                    sendPacketMethod.invoke(connection, packetC.newInstance(packet.mapId, (byte) 4, false, new ArrayList<>(), MapPatchC.newInstance(packet.sX, packet.sY, packet.fX, packet.fY, packet.color)));
                }

            } else if (MCVersion.greaterThanEqual(MCVersion.v1_14_R1)) {
                for (MapPacket packet : packets){
                    sendPacketMethod.invoke(connection, packetC.newInstance(packet.mapId, (byte) 4, false, false, new ArrayList<>(), packet.color, packet.sX, packet.sY, packet.fX, packet.fY));
                }

            } else {
                for (MapPacket packet : packets){
                    sendPacketMethod.invoke(connection, packetC.newInstance(packet.mapId, (byte) 4, false, new ArrayList<>(), packet.color, packet.sX, packet.sY, packet.fX, packet.fY));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
