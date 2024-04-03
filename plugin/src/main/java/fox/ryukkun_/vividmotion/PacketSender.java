package fox.ryukkun_.vividmotion;

import fox.ryukkun_.MapPacket;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PacketSender {
    private static Constructor<?> mapPacketCo = null;
    private static Class<?> craftPlayerClass = null;
    private static Method sendPacketMethod = null;
    private static Field connectionF = null;
    private static Constructor<?> mapPatchCo = null;

    static {
        try {
            if (MCVersion.greaterThanEqual(MCVersion.v1_17_R1)) {
                Class<?> mapPacketClass =Class.forName("net.minecraft.network.protocol.game.PacketPlayOutMap");
                Class<?> connectionClass = Class.forName("net.minecraft.server.network.PlayerConnection");
                Class<?> playerClass = Class.forName("net.minecraft.server.level.EntityPlayer");
                Class<?> bClass = Class.forName("net.minecraft.world.level.saveddata.maps.WorldMap$b");
                Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");

                sendPacketMethod = Reflection.findMethod(connectionClass, void.class, packetClass);
                connectionF = Reflection.findField(playerClass, connectionClass);
                mapPatchCo = bClass.getConstructor(int.class, int.class, int.class, int.class, byte[].class);
                mapPacketCo = mapPacketClass.getConstructor(int.class, byte.class, boolean.class, Collection.class, bClass);

            } else if (MCVersion.greaterThanEqual(MCVersion.v1_14_R1)) {
                Class<?> c = Class.forName(MCVersion.getNMS()+"PacketPlayOutMap");
                mapPacketCo = c.getConstructor(int.class, byte.class, boolean.class, boolean.class, Collection.class, byte[].class, int.class, int.class, int.class, int.class);
                sendPacketMethod = Class.forName(MCVersion.getNMS()+"PlayerConnection").getMethod("sendPacket", Class.forName(MCVersion.getNMS()+"Packet"));
                connectionF = Class.forName(MCVersion.getNMS()+"EntityPlayer").getField("playerConnection");

            } else {
                Class<?> c = Class.forName(MCVersion.getNMS()+"PacketPlayOutMap");
                mapPacketCo = c.getConstructor(int.class, byte.class, boolean.class, Collection.class, byte[].class, int.class, int.class, int.class, int.class);
                sendPacketMethod = Class.forName(MCVersion.getNMS()+"PlayerConnection").getMethod("sendPacket", Class.forName(MCVersion.getNMS()+"Packet"));
                connectionF = Class.forName(MCVersion.getNMS()+"EntityPlayer").getField("playerConnection");
            }
            craftPlayerClass = Class.forName(MCVersion.getCB()+"entity.CraftPlayer");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPacket(Player player, List<MapPacket> packets) {
        try {
            Object connection = connectionF.get(craftPlayerClass.getMethod("getHandle").invoke(player));
            List<?> icon = new ArrayList<>();

            if (MCVersion.greaterThanEqual(MCVersion.v1_17_R1)) {
                for (MapPacket packet : packets){
                    sendPacketMethod.invoke(connection, mapPacketCo.newInstance(packet.mapId, (byte) 4, false, icon, mapPatchCo.newInstance(packet.sX, packet.sY, packet.fX, packet.fY, packet.color)));
                }

            } else if (MCVersion.greaterThanEqual(MCVersion.v1_14_R1)) {
                for (MapPacket packet : packets){
                    sendPacketMethod.invoke(connection, mapPacketCo.newInstance(packet.mapId, (byte) 4, false, false, icon, packet.color, packet.sX, packet.sY, packet.fX, packet.fY));
                }

            } else {
                for (MapPacket packet : packets){
                    sendPacketMethod.invoke(connection, mapPacketCo.newInstance(packet.mapId, (byte) 4, false, icon, packet.color, packet.sX, packet.sY, packet.fX, packet.fY));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
