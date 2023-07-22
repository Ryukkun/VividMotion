package foxy.ryukkun_.vividmotion.videoutil;

import java.awt.image.BufferedImage;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.map.MapPalette;

public class MapConverter {
    public static final Short[] color = get_all_color();
    public static final Short[] fixedColor = Arrays.copyOfRange(color, 4*3, color.length);

    public static byte[] toConvert(BufferedImage buff){

        int i, color_index;
        int index = -1;
        int width = buff.getWidth(), height = buff.getHeight();
        int[] diff = new int[3];
        int[] pixel = new int[3];
        byte[] map_format = new byte[width*height];

        int[] pixel_data = buff.getRGB(0, 0, width, height, null, 0, width);


        for (int y = 0; y < height; y++) {
            diff[0] = 0;
            diff[1] = 0;
            diff[2] = 0;

            for (int x = 0; x < width; x++) {
                index++;
                pixel[0] = (pixel_data[index] >> 16 & 0xff) + diff[0];
                pixel[1] = (pixel_data[index] >> 8 & 0xff) + diff[1];
                pixel[2] = (pixel_data[index] & 0xff) + diff[2];
                //System.out.print("b:"+b+" g:"+g+" r:"+r+" bgr:"+rgb);


                color_index = get_nearest_fixedColor(pixel[0], pixel[1], pixel[2]);

                map_format[index] = (byte)(color_index+4);

                color_index *= 3;
                for (i = 0; i < 3; i++) {
                    if (0 < pixel[i]) {
                        diff[i] = pixel[i] - fixedColor[color_index+i];
                    } else {
                        diff[i] = pixel[i] + fixedColor[color_index+i];
                    }
                }
            }
        }
        return map_format;
    }

    public static int get_nearest_fixedColor(int r, int g, int b){
        int last_total = 200000;
        int index = 0;
        int color_index = 0;
        int total, n;

        for (int i = 0, color_count = fixedColor.length/3; i < color_count; i++) {
            total = 0;

            n = fixedColor[index++] - r;
            total += n * n;

            n = fixedColor[index++] - g;
            total += n * n;

            n = fixedColor[index++] - b;
            total += n * n;


            if (total < last_total) {
                last_total = total;
                color_index = i;
            }
        }
        return color_index;
    }

    public static int get_nearest_color(int r, int g, int b){
        int last_total = 200000;
        int index = 0;
        int color_index = 0;
        int total, n;

        for (int i = 0, color_count = color.length/3; i < color_count; i++) {
            total = 0;

            n = color[index++] - r;
            total += n * n;

            n = color[index++] - g;
            total += n * n;

            n = color[index++] - b;
            total += n * n;


            if (total < last_total) {
                last_total = total;
                color_index = i;
            }
        }
        return color_index;
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
