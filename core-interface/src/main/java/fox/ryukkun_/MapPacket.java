package fox.ryukkun_;

public class MapPacket {
    public final int mapId;
    public final int fX;
    public final int fY;
    public final int sX;
    public final int sY;
    public final byte[] color;
    public MapPacket(int mapId, byte[] color, int sX, int sY, int fX, int fY){
        this.mapId = mapId;
        this.color = color;
        this.sX = sX;
        this.sY = sY;
        this.fX = fX;
        this.fY = fY;
    }

    public MapPacket(int mapId, byte[] color){
        this.mapId = mapId;
        this.color = color;
        this.sX = 0;
        this.sY = 0;
        this.fX = 128;
        this.fY = 128;
    }
}
