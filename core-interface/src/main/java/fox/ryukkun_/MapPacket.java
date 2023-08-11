package fox.ryukkun_;

public class MapPacket {
    public int mapId, fX, fY, sX, sY;
    public boolean fullChange, notChange;
    public byte[] color;
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


    public MapPacket(int mapId, byte[] oldColor, byte[] newColor){
        this.mapId = mapId;
        arrayCheck(oldColor, newColor);

        // set pixelData
        if (fullChange) color = newColor;
        else if (notChange) color = null;
        else color = getPixelData(newColor);
    }


    private void arrayCheck(byte[] oldByte, byte[] NewByte) {
        int equalNum = 3;
        int startX = 128 - equalNum;
        int startY = 128;
        int endY = -1;

        for (int x = 0; x < equalNum; x++) {
            for (int y = 0; y < 128; y++) {
                int i = y * 128 + 127 - x;
                if (oldByte[i] != NewByte[i]) {
                    if (y < startY) startY = y;
                    if (endY < y) endY = y;
                }
            }
            if (startY == 0 && endY == 127) {
                break;
            } else if (endY == -1) {
                notChange = true;
                return;
            }
        }

        for (int y = startY; y <= endY; y++) {
            int breakCache = 0;
            for (int x = startX-1; 0 <= x; x--) {
                int i = y * 128 + x;
                if (oldByte[i] != NewByte[i]) {
                    startX = x;
                    breakCache = 0;
                } else {
                    breakCache++;
                    if (equalNum <= breakCache) break;
                }
            }
            if (startX == 0) break;
        }

        if (startX == 0 && startY == 0 && endY == 127) fullChange = true;
        this.sX = startX;
        this.sY = startY;
        this.fX = 128-startX;
        this.fY = endY+1-startY;
    }


    private byte[] getPixelData(byte[] rawPixelData){
        int endX = sX + fX;
        int endY = sY + fY;
        byte[] pixelData = new byte[fX*fY];

        int index = 0;
        for (int y = sY; y < endY; y++) {
            for (int x = sX; x < endX; x++) {
                int i = y * 128 + x;
                pixelData[index++] = rawPixelData[i];
            }
        }
        return pixelData;
    }
}
