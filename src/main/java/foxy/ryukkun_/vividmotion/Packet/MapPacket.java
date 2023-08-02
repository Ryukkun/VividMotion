package foxy.ryukkun_.vividmotion.Packet;

public class MapPacket {
    public int mapId, fX, fY, sX, sY;
    public byte[] color;
    public MapPacket(int mapId, byte[] color, int sX, int sY, int fX, int fY){
        this.mapId = mapId;
        this.color = color;
        this.sX = sX;
        this.sY = sY;
        this.fX = fX;
        this.fY = fY;
    }
}
