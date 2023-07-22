package foxy.ryukkun_.vividmotion.videoutil;

import java.util.ArrayList;
import java.util.List;

public class MapsData {
    // Map Count (Height, Width)
    public int height, width, m_height, m_width;
    public double frameRate;
    public boolean is_loaded = false;
    // Byte[Frame][height*width]
    public final List<byte[]> map_pixel = new ArrayList<>();
    public byte background_color;

    public MapsData(int width, int height, double frameRate){
        setBackgroundColor(0,0,0);
        this.frameRate = frameRate;
        this.width = width;
        this.height = height;
        this.m_width = width % 128 == 0 ? width/128 : width/128+1;
        this.m_height = height % 128 == 0 ? height/128 : height/128+1;
    }

    public void addFrame(byte[] bytes){
        map_pixel.add(bytes);
    }

    public void setBackgroundColor(int r, int g, int b){
        background_color = (byte)MapConverter.get_nearest_color(r,g,b);
    }

    public byte[][] getMapDatas(int frame){
        int m_index, p_index, px, py;
        int height_diff = (m_height*128 - height)/2;
        int width_diff = (m_width*128 - width)/2;
        byte[][] maps = new byte[m_width*m_height][128*128];

        for (int y = 0, y_limit = m_height*128; y < y_limit; y++){
            for (int x = 0, x_limit = m_width*128; x < x_limit; x++){
                p_index = (y%128 * 128) + (x%128);
                m_index = (y/128 * m_width) + (x/128);
                px = x-width_diff;
                py = y-height_diff;
                if (py < 0 || px < 0 || height <= py || width <= px ){
                    maps[m_index][p_index] = background_color;
                }else {
                    maps[m_index][p_index] = map_pixel.get(frame)[py*width+px];
                }
            }
        }

        return  maps;
    }

    public void loaded(){
        is_loaded = true;
    }
}
