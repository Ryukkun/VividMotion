package foxy.ryukkun_.vividmotion.videoutil;

import foxy.ryukkun_.vividmotion.MapUtils;
import foxy.ryukkun_.vividmotion.VividMotion;
import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.map.CraftMapView;
import org.bukkit.map.MapView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapsData {
    public Data data;
    public FFmpegSource ffs;
    public World world;

    public MapsData(FFmpegSource ffs, World world){
        data = new Data(ffs, world);
        setBackgroundColor(0,0,0);
        this.ffs = ffs;
        this.world = world;

        new Thread(this::loadFFS).start();
    }

    public MapsData(File path) {
        try (FileInputStream f = new FileInputStream(path);
             ObjectInputStream in = new ObjectInputStream(f)) {

            data = (Data) in.readObject();

            VividMotion.mapsDataList.add(this);
            new VideoPlayer(this).start();

        } catch (IOException | ClassNotFoundException e) {
            Bukkit.getLogger().warning(e.toString());
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


        // Loaded
        data.is_loaded = true;


        if (data.map_pixel.size() == 1){
            // isPicture
            data.isPicture = true;

            byte[][] pixelData = getMapData();
            for (int i = 0; i < data.mapIds.length; i++ ){
                MapView view = Bukkit.getMap(data.mapIds[i]);

                try {
                    view.getClass().getDeclaredField("");
                    view.colors = pixelData[i];
                } catch (NoSuchFieldException e) {

                }
                view.addRenderer(new MapRenderPicture(worldMap));
            }

        } else{
            // isVideo
            data.isPicture = false;

            VividMotion.mapsDataList.add(this);
            new VideoPlayer(this).start();
        }
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
        int m_index, p_index, px, py;
        int height = data.height, width = data.width;
        int m_height = data.m_height, m_width = data.m_width;

        int height_diff = (m_height*128 - height)/2;
        int width_diff = (m_width*128 - width)/2;
        byte[][] maps = new byte[m_width*m_height][128*128];

        for (int y = 0, y_limit = m_height*128; y < y_limit; y++){
            for (int x = 0, x_limit = m_width*128; x < x_limit; x++){
                p_index = (y%128 * 128) + (x%128);
                m_index = (y/128 * m_width) + (x/128);
                px = x-width_diff;
                py = y-height_diff;
                maps[m_index][p_index] = (py < 0 || px < 0 || height <= py || width <= px ) ? data.background_color : bytes[py*width+px];
            }
        }

        data.map_pixel.add(maps);
    }

    public void setBackgroundColor(int r, int g, int b){
        data.background_color = (byte)MapConverter.get_nearest_color(r,g,b);
    }

    public byte[][] getMapData(){
        data.nowFrame++;
        return getMapData(data.nowFrame);
    }


    public byte[][] getMapData(int frame){
        if (data.map_pixel.size() <= frame){
            frame = 0;
        }
        data.nowFrame = frame;
        return data.map_pixel.get(frame);
    }




    public static class Data implements Serializable {
        // Map Count (Height, Width)
        public int height, width, m_height, m_width;
        public double videoFrameRate, setFrameRate;
        public boolean is_loaded = false;
        // Byte[Frame][height*width]
        public final List<byte[][]> map_pixel = new ArrayList<>();
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
                mapIds[i] = MapUtils.createMap(world).getId();
            }
        }
    }
}
