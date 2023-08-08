package foxy.ryukkun_.vividmotion.imageutil;

import java.awt.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.map.MapPalette;
import org.bytedeco.javacv.Frame;

public class ImageConverter {
    public static final short[] color;
    private static final short[] fixedColor;


    // Cache設定
    // DIV : 2だったら1個とばしで計算
    // rowDif : 両端のキャッシュ量 左右合計
    private static final int DIV = 4;
    private static final int DIV_SHIFT = DIV/2;
    private static final int rowDif = 128;
    private static final int DIV_RowDif = rowDif / DIV;
    private static final int oneSideDif = rowDif/2;
    private static final int DIV_oneSideDif = oneSideDif / DIV;
    private static final int leftLimit = -oneSideDif;
    private static final int rightLimit = 256 + oneSideDif;
    private static final int DIV_Row = 256 / DIV + DIV_RowDif;
    private static final int DIV_Row2 = DIV_Row *DIV_Row;
    private static final int DIV_Row3 = DIV_Row2 * DIV_Row;
    private static final short[] colorCache;

    static{
        Short[] _c = get_all_color();
        color = new short[_c.length];
        for (int i = 0; i < _c.length; i++){
            color[i] = _c[i];
        }
        fixedColor = Arrays.copyOfRange(color, 4*3, color.length);

        colorCache = calcCache();

    }



    public static byte[] toConvert(Frame frame){

        int index = 0;
        int width = frame.imageWidth, height = frame.imageHeight;
        int[] diff = new int[3];
        int[] pixel = new int[3];
        byte[] map_format = new byte[width*height];

        ByteBuffer buff = (ByteBuffer) frame.image[0];
        buff.rewind();

        for (int y = 0; y < height; y++) {
            diff[0] = 0;
            diff[1] = 0;
            diff[2] = 0;

            for (int x = 0; x < width; x++) {
                pixel[0] = (buff.get() & 0xFF) + diff[0];
                pixel[1] = (buff.get() & 0xFF) + diff[1];
                pixel[2] = (buff.get() & 0xFF) + diff[2];

                short color_index = (leftLimit <= pixel[0] && pixel[0] < rightLimit && leftLimit <= pixel[1] && pixel[1] < rightLimit && leftLimit <= pixel[2] && pixel[2] < rightLimit)
                        ? colorCache[ ((pixel[0]>>DIV_SHIFT)+DIV_oneSideDif) + (((pixel[1]>>DIV_SHIFT)+DIV_oneSideDif)*DIV_Row) + (((pixel[2]>>DIV_SHIFT)+DIV_oneSideDif)*DIV_Row2)]
                        : get_nearest_fixedColor(pixel[0], pixel[1], pixel[2]);

                map_format[index++] = (byte)(color_index+4);
                color_index *= 3;
                for (int i = 0; i < 3; i++) {
                    diff[i] = 0 <= pixel[i] ? pixel[i] - fixedColor[color_index+i] : pixel[i] + fixedColor[color_index+i];
                }

                if (255 < diff[0] || 255 < diff[1] || 255 < diff[2]){
                    diff[0] >>= 1;
                    diff[1] >>= 1;
                    diff[2] >>= 1;
                }
            }
        }
        return map_format;
    }


    private static short[] calcCache(){

        int rr, gg, bb;
        short[] byte_ = new short[DIV_Row3];
        //short[] c = fixedColor;

        for (int r = 0; r < DIV_Row; r++){
            rr = r*DIV-oneSideDif;
            Bukkit.getLogger().info(Integer.toString(r));

            for (int g = 0; g < DIV_Row; g++){
                gg = g*DIV-oneSideDif;

                for (int b = 0; b < DIV_Row; b++){
                    bb = b*DIV-oneSideDif;

                    byte_[r + (g * DIV_Row) + (b * DIV_Row2)] = get_nearest_color(rr, gg, bb, fixedColor);

                }
            }
        }
        return byte_;
    }



    private static short get_nearest_fixedColor(int r, int g, int b){
        return get_nearest_color(r, g, b, fixedColor);
    }


    private static short get_nearest_color(int r, int g, int b, short[] c){

        int last_total = Integer.MAX_VALUE;
        int index = 0;
        short color_index = 0;

        for (short i = 0, color_count = (short) (c.length/3); i < color_count; i++) {

            int total = 0;
            int n;
            n = c[index++] - r;
            total += n * n;
            n = c[index++] - g;
            total += n * n;
            n = c[index++] - b;
            total += n * n;

            if (total < last_total) {
                last_total = total;
                color_index = i;
            }
        }
        return color_index;
    }



    public static short get_nearest_color(int r, int g, int b){
        return get_nearest_color(r, g, b, color);
    }



    private static Short[] get_all_color(){
        List<Short> res = new ArrayList<>();
        Color col;
        try {
            for (byte i = 0; i != -1; i++) {
                col = MapPalette.getColor(i);
                res.add((short) col.getRed());
                res.add((short) col.getGreen());
                res.add((short) col.getBlue());
            }
        }catch (Exception ignored){}
        return res.toArray(new Short[0]);
    }
}
