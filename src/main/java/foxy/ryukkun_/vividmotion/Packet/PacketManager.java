package foxy.ryukkun_.vividmotion.Packet;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.entity.Player;

public interface PacketManager {

    static PacketManager getManager(){
        NBTEditor.MinecraftVersion version = NBTEditor.getMinecraftVersion();
        if (version.equals(NBTEditor.MinecraftVersion.v1_12)){
            return new Packet_1_12();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_13)) {
            return new Packet_1_13();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_14)) {
            return new Packet_1_14();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_15)) {
            return new Packet_1_15();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_16)) {
            return new Packet_1_16();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_17)) {
            return new Packet_1_17();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_18_R1)) {
            return new Packet_1_18_R1();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_18_R2)) {
            return new Packet_1_18_R2();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_19_R1)) {
            return new Packet_1_19_R1();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_19_R2)) {
            return new Packet_1_19_R2();
        } else if (version.equals(NBTEditor.MinecraftVersion.v1_19_R3)) {
            return new Packet_1_19_R3();
        }

        return null;
    }



    void sendPacket(Player player, MapPacket... packet);
}
