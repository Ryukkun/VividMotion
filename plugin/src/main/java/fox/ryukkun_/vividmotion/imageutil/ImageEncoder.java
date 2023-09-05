package fox.ryukkun_.vividmotion.imageutil;

import java.awt.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fox.ryukkun_.vividmotion.ConfigManager;
import org.bukkit.map.MapPalette;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class ImageEncoder {
    public static short[] color;
    private static short[] fixedColor;
    private static final Java2DFrameConverter java2d = new Java2DFrameConverter();

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
    private static short[] colorCache;


    public static void load(){
        new Thread(() -> {
            Short[] _c = get_all_color();
            color = new short[_c.length];
            for (int i = 0; i < _c.length; i++){
                color[i] = _c[i];
            }
            fixedColor = Arrays.copyOfRange(color, 4*3, color.length);

            colorCache = calcCache();

        }).start();
    }

    public static byte[] toConvert(Frame frame) {
        EncodeType encode;
        try {
            encode = ConfigManager.getEncode();
        } catch (Exception e) {
            encode = EncodeType.EDM_Mk3;
        }
        return toConvert(frame, encode);
    }

    public static byte[] toConvert(Frame frame, EncodeType encode) {
        if (encode.equals( EncodeType.EDM)) {
            return encodeEDM( frame);
        } else if (encode.equals( EncodeType.EDM_Mk3)) {
            return encodeEDM_Mk3( frame);
        } else {
            return encodeNearest( frame);
        }
    }


    public static byte[] encodeEDM(Frame frame){
        int index = 0;
        int width = frame.imageWidth, height = frame.imageHeight;

        short[][] pixel = new short[width*height][3];
        byte[] map_format = new byte[width*height];


        int[] buffer = java2d.convert(frame).getRGB(0, 0, width, height, null, 0, width);


        for (int y = 0; y <  height; y++) {
            for (int x = 0; x < width; x++) {
                pixel[index][0] = (short) (buffer[index] >> 16 & 0xff);
                pixel[index][1] = (short) (buffer[index] >> 8 & 0xff);
                pixel[index][2] = (short) (buffer[index] & 0xff);
                index++;
            }
        }

        index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                short color_index = (leftLimit <= pixel[index][0] && pixel[index][0] < rightLimit && leftLimit <= pixel[index][1] && pixel[index][1] < rightLimit && leftLimit <= pixel[index][2] && pixel[index][2] < rightLimit)
                        ? colorCache[ ((pixel[index][0]>>DIV_SHIFT)+DIV_oneSideDif) + (((pixel[index][1]>>DIV_SHIFT)+DIV_oneSideDif)*DIV_Row) + (((pixel[index][2]>>DIV_SHIFT)+DIV_oneSideDif)*DIV_Row2)]
                        : get_nearest_fixedColor(pixel[index][0], pixel[index][1], pixel[index][2]);

                map_format[index] = (byte)(color_index+4);
                color_index *= 3;

                boolean right = x+1 != width;
                boolean left = x-1 != -1;
                boolean under = y+1 != height;
                for (int i = 0; i < 3; i++) {
                    int s = (0 <= pixel[index][i] ? pixel[index][i] - fixedColor[color_index+i] : pixel[index][i] + fixedColor[color_index+i]) >> 4;

                    if (right) {
                        pixel[index+1][i] += (short) (s * 7);
                    }
                    if (under){
                        if (right) {
                            pixel[index+width+1][i] += (short) s;
                        }
                        if (left) {
                            pixel[index+width-1][i] += (short) (s * 3);
                        }
                        pixel[index+width][i] += (short) (s * 5);
                    }

                }

                index++;
            }
        }
        return map_format;
    }



    public static byte[] encodeEDM_Mk3(Frame frame){
        int index = 0;
        int width = frame.imageWidth, height = frame.imageHeight;
        int[] diff = {0, 0, 0};
        int[] pixel = new int[3];
        byte[] map_format = new byte[width*height];
        Random random = new Random(1717289);

        int[] buffer = java2d.convert(frame).getRGB(0, 0, width, height, null, 0, width);


        for (int y = 0; y < height; y++) {
            diff[0] = random.nextInt(40)-20;
            diff[1] = random.nextInt(40)-20;
            diff[2] = random.nextInt(40)-20;

            for (int x = 0; x < width; x++) {
                pixel[0] = (buffer[index] >> 16 & 0xff) + diff[0];
                pixel[1] = (buffer[index] >> 8 & 0xff) + diff[1];
                pixel[2] = (buffer[index] & 0xff) + diff[2];

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


    public static byte[] encodeNearest(Frame frame){
        int index = 0;
        int width = frame.imageWidth, height = frame.imageHeight;
        byte[] map_format = new byte[width*height];

        int[] buffer = java2d.convert(frame).getRGB(0, 0, width, height, null, 0, width);


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map_format[index] = (byte) (colorCache[(((buffer[index] >> 16 & 0xff)>>DIV_SHIFT)+DIV_oneSideDif) +
                        ((((buffer[index] >> 8 & 0xff)>>DIV_SHIFT)+DIV_oneSideDif)*DIV_Row) +
                        ((((buffer[index] & 0xff)>>DIV_SHIFT)+DIV_oneSideDif)*DIV_Row2)] + 4);
                index++;
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



    public enum EncodeType {
        EDM("誤差拡散"),
        EDM_Mk3("誤差拡散.Mk3"),
        Nearest("近似");


        public final String name;
        EncodeType(String arg) {
            name = arg;
        }

        public static EncodeType nameOf(String name) throws Exception {
            for (EncodeType v : values()) {
                if (v.name.equalsIgnoreCase( name.trim())) {
                    return v;
                }
            }
            throw new Exception( "一致するEncodeTypeが見つかりませんでした。");
        }
    }
}
