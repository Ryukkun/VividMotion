package fox.ryukkun_.vividmotion.screen;

import fox.ryukkun_.MapPacket;
import fox.ryukkun_.vividmotion.MCVersion;

import java.util.ArrayList;
import java.util.List;

public class VideoPacket {
    private static final boolean lessThan1_16 = MCVersion.lessThanEqual( MCVersion.v1_16_R3);
    private int sX = -1;
    private int sY = -1;
    private int fX = -1;
    private int fY = -1;
    public boolean noChange = false;


    public VideoPacket(int sX, int sY, int fX, int fY) {
        this.sX = sX;
        this.sY = sY;
        this.fX = fX;
        this.fY = fY;
    }

    public VideoPacket(boolean noChange) {
        this.noChange = true;
    }

    public MapPacket getMapPacket(int mapId, byte[] color) {
        if (noChange) {
            return null;
        } else if (lessThan1_16) {
            return new MapPacket(mapId, color, sX, sY, fX+1-sX, fY+1-sY);
        } else {
            return new MapPacket(mapId, getTrimmedColor(color, sX, sY, fX, fY), sX, sY, fX+1-sX, fY+1-sY);
        }
    }


    public static VideoPacket[] getDifference(byte[][] oldByte, byte[][] newByte) {
        List<VideoPacket> packetList = new ArrayList<>();
        for (int i = 0; i < oldByte.length; i++) {
            packetList.add( getDifference(oldByte[i], newByte[i]));
        }
        return packetList.toArray(new VideoPacket[0]);
    }

    public static VideoPacket getDifference(byte[] oldByte, byte[] newByte) {
        int sX = 128;
        int sY = 128;
        int fX = 0;
        int fY = 0;

        int index = 0;
        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                if (oldByte[index] != newByte[index]) {
                    sX = Math.min(x, sX);
                    sY = Math.min(y, sY);
                    fX = Math.max(x, fX);
                    fY = Math.max(y, fY);
                }
                index++;
            }
        }
        if (sX == 128) {
            return new VideoPacket(true);
        }
        return new VideoPacket(sX, sY, fX, fY);
    }

    private static byte[] getTrimmedColor(byte[] color, int sX, int sY, int fX, int fY) {
        int dX = fX + 1 - sX;
        int dY = fY + 1 - sY;
        byte[] result = new byte[dX*dY];

        int index = 0;
        for (int y = sY; y <= fY; y++) {
            for (int x = sX; x <= fX; x++) {
                result[index++] = color[y * 128 + x];
            }
        }
        return result;
    }
}
