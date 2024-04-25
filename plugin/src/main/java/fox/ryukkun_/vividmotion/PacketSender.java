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
    private final static PacketSendInterface connection;

    static {
            if (MCVersion.greaterThanEqual(MCVersion.v1_20_R4)) {
                connection = new v1_20_R4();

            } else if (MCVersion.greaterThanEqual(MCVersion.v1_17_R1)) {
                connection = new v1_17_R1();

            } else if (MCVersion.greaterThanEqual(MCVersion.v1_14_R1)) {
                connection = new v1_14_R1();

            } else {
                connection = new lessThan_v1_14_R1();
            }
    }

    public static void sendPacket(Player player, List<MapPacket> packets) {
        connection.sendPacket(player, packets);
    }



    private interface PacketSendInterface {
        void sendPacket(Player player, List<MapPacket> packets);
    }

    private static class v1_20_R4 implements PacketSendInterface {
        private static Constructor<?> mapPacketCo = null;
        private static Class<?> craftPlayerClass = null;
        private static Method sendPacketMethod = null;
        private static Field connectionF = null;
        private static Constructor<?> mapPatchCo = null;
        private static Constructor<?> mapIdCo = null;

        static {
            try {
                final Class<?> mapPacketClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutMap");
                final Class<?> connectionClass = Class.forName("net.minecraft.server.network.PlayerConnection");
                final Class<?> playerClass = Class.forName("net.minecraft.server.level.EntityPlayer");
                final Class<?> bClass = Class.forName("net.minecraft.world.level.saveddata.maps.WorldMap$b");
                final Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");
                final Class<?> mapIdClass = Class.forName("net.minecraft.world.level.saveddata.maps.MapId");

                mapIdCo = mapIdClass.getConstructor(int.class);
                craftPlayerClass = Class.forName(MCVersion.getCB()+"entity.CraftPlayer");
                sendPacketMethod = Reflection.findMethod(connectionClass, void.class, packetClass);
                connectionF = Reflection.findField(playerClass, connectionClass);
                mapPatchCo = bClass.getConstructor(int.class, int.class, int.class, int.class, byte[].class);
                mapPacketCo = mapPacketClass.getConstructor(mapIdClass, byte.class, boolean.class, Collection.class, bClass);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendPacket(Player player, List<MapPacket> packets) {
            try {
                Object connection = connectionF.get(craftPlayerClass.getMethod("getHandle").invoke(player));
                List<?> icon = new ArrayList<>();

                for (MapPacket packet : packets){
                    sendPacketMethod.invoke(connection, mapPacketCo.newInstance(mapIdCo.newInstance(packet.mapId), (byte) 4, false, icon, mapPatchCo.newInstance(packet.sX, packet.sY, packet.fX, packet.fY, packet.color)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static class v1_17_R1 implements PacketSendInterface {
        private static Constructor<?> mapPacketCo = null;
        private static Class<?> craftPlayerClass = null;
        private static Method sendPacketMethod = null;
        private static Field connectionF = null;
        private static Constructor<?> mapPatchCo = null;

        static {
            try {
                Class<?> mapPacketClass =Class.forName("net.minecraft.network.protocol.game.PacketPlayOutMap");
                Class<?> connectionClass = Class.forName("net.minecraft.server.network.PlayerConnection");
                Class<?> playerClass = Class.forName("net.minecraft.server.level.EntityPlayer");
                Class<?> bClass = Class.forName("net.minecraft.world.level.saveddata.maps.WorldMap$b");
                Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");

                sendPacketMethod = Reflection.findMethod(connectionClass, void.class, packetClass);
                connectionF = Reflection.findField(playerClass, connectionClass);
                mapPatchCo = bClass.getConstructor(int.class, int.class, int.class, int.class, byte[].class);
                mapPacketCo = mapPacketClass.getConstructor(int.class, byte.class, boolean.class, Collection.class, bClass);
                craftPlayerClass = Class.forName(MCVersion.getCB()+"entity.CraftPlayer");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendPacket(Player player, List<MapPacket> packets) {
            try {
                Object connection = connectionF.get(craftPlayerClass.getMethod("getHandle").invoke(player));
                List<?> icon = new ArrayList<>();

                for (MapPacket packet : packets){
                    sendPacketMethod.invoke(connection, mapPacketCo.newInstance(packet.mapId, (byte) 4, false, icon, mapPatchCo.newInstance(packet.sX, packet.sY, packet.fX, packet.fY, packet.color)));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static class v1_14_R1 implements PacketSendInterface {
        private static Constructor<?> mapPacketCo = null;
        private static Class<?> craftPlayerClass = null;
        private static Method sendPacketMethod = null;
        private static Field connectionF = null;

        static {
            try {
                Class<?> c = Class.forName(MCVersion.getNMS()+"PacketPlayOutMap");
                mapPacketCo = c.getConstructor(int.class, byte.class, boolean.class, boolean.class, Collection.class, byte[].class, int.class, int.class, int.class, int.class);
                sendPacketMethod = Class.forName(MCVersion.getNMS()+"PlayerConnection").getMethod("sendPacket", Class.forName(MCVersion.getNMS()+"Packet"));
                connectionF = Class.forName(MCVersion.getNMS()+"EntityPlayer").getField("playerConnection");
                craftPlayerClass = Class.forName(MCVersion.getCB()+"entity.CraftPlayer");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendPacket(Player player, List<MapPacket> packets) {
            try {
                Object connection = connectionF.get(craftPlayerClass.getMethod("getHandle").invoke(player));
                List<?> icon = new ArrayList<>();

                for (MapPacket packet : packets){
                    sendPacketMethod.invoke(connection, mapPacketCo.newInstance(packet.mapId, (byte) 4, false, false, icon, packet.color, packet.sX, packet.sY, packet.fX, packet.fY));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static class lessThan_v1_14_R1 implements PacketSendInterface {
        private static Constructor<?> mapPacketCo = null;
        private static Class<?> craftPlayerClass = null;
        private static Method sendPacketMethod = null;
        private static Field connectionF = null;

        static {
            try {
                Class<?> c = Class.forName(MCVersion.getNMS()+"PacketPlayOutMap");
                mapPacketCo = c.getConstructor(int.class, byte.class, boolean.class, Collection.class, byte[].class, int.class, int.class, int.class, int.class);
                sendPacketMethod = Class.forName(MCVersion.getNMS()+"PlayerConnection").getMethod("sendPacket", Class.forName(MCVersion.getNMS()+"Packet"));
                connectionF = Class.forName(MCVersion.getNMS()+"EntityPlayer").getField("playerConnection");
                craftPlayerClass = Class.forName(MCVersion.getCB()+"entity.CraftPlayer");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendPacket(Player player, List<MapPacket> packets) {
            try {
                Object connection = connectionF.get(craftPlayerClass.getMethod("getHandle").invoke(player));
                List<?> icon = new ArrayList<>();

                for (MapPacket packet : packets){
                    sendPacketMethod.invoke(connection, mapPacketCo.newInstance(packet.mapId, (byte) 4, false, icon, packet.color, packet.sX, packet.sY, packet.fX, packet.fY));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
