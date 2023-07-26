package foxy.ryukkun_.vividmotion.videoutil;

import foxy.ryukkun_.vividmotion.VividMotion;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapsData {
    public Data data;
    public FFmpegSource ffs;

    public MapsData(FFmpegSource ffs, World world){
        setBackgroundColor(0,0,0);
        data = new Data(ffs, world);
        this.ffs = ffs;
    }

    public MapsData(File path) {
        try (FileInputStream f = new FileInputStream(path);
             ObjectInputStream in = new ObjectInputStream(f)) {

            data = (Data) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadFFS(){
        // Please run with async
        byte[] frame;
        while (true){
            frame = ffs.read();
            if (frame == null){
                break;
            }
            addFrame(frame);
        }
        loaded();
    }

    public boolean setFrameRate(double fr){
        if (0 < fr){
            data.setFrameRate = fr;
            return true;
        }
        return false;
    }

    public double getFrameRate(){
        return Math.min(data.setFrameRate, data.videoFrameRate);
    }

    public void resume(){
        data.isPausing = false;
    }

    public void pause(){
        data.isPausing = true;
    }

    public boolean isPausing(){
        return data.isPausing;
    }


    public void addFrame(byte[] bytes){
        data.map_pixel.add(bytes);
    }

    public void setBackgroundColor(int r, int g, int b){
        data.background_color = (byte)MapConverter.get_nearest_color(r,g,b);
    }

    public byte[][] getMapData(){
        data.nowFrame++;
        return getMapData(data.nowFrame);
    }


    public byte[][] getMapData(int frame){
        int m_index, p_index, px, py;
        int height = data.height, width = data.width;
        int m_height = data.m_height, m_width = data.m_width;

        int height_diff = (m_height*128 - height)/2;
        int width_diff = (m_width*128 - width)/2;
        byte[][] maps = new byte[m_width*m_height][128*128];

        if (data.map_pixel.size() <= frame){
            frame = 0;
        }
        data.nowFrame = frame;

        for (int y = 0, y_limit = m_height*128; y < y_limit; y++){
            for (int x = 0, x_limit = m_width*128; x < x_limit; x++){
                p_index = (y%128 * 128) + (x%128);
                m_index = (y/128 * m_width) + (x/128);
                px = x-width_diff;
                py = y-height_diff;
                if (py < 0 || px < 0 || height <= py || width <= px ){
                    maps[m_index][p_index] = data.background_color;
                }else {
                    maps[m_index][p_index] = data.map_pixel.get(frame)[py*width+px];
                }
            }
        }

        return  maps;
    }

    public void loaded(){
        data.is_loaded = true;

        if (data.map_pixel.size() == 1){
            data.isPicture = true;
        }
    }


    public static class Data implements Serializable {
        // Map Count (Height, Width)
        public int height, width, m_height, m_width;
        public double videoFrameRate, setFrameRate;
        public boolean is_loaded = false;
        // Byte[Frame][height*width]
        public final List<byte[]> map_pixel = new ArrayList<>();
        public int[] mapIds;
        public byte background_color;
        public int nowFrame = -1;
        public boolean isPausing = false;
        public boolean isPicture;

        public Data(FFmpegSource ffs, World world){
            height = ffs.height;
            width = ffs.width;
            videoFrameRate = ffs.frameRate;
            m_width = width % 128 == 0 ? width/128 : width/128+1;
            m_height = height % 128 == 0 ? height/128 : height/128+1;
            setFrameRate = 20.0;


            mapIds = new int[m_height*m_width];
            for (int i = 0; i < m_height*m_width; i++){
                mapIds[i] = Bukkit.createMap(world).getId();
            }
        }
    }
}
