package foxy.ryukkun_.vividmotion.screen;

import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.imageutil.FFmpegSource;
import foxy.ryukkun_.vividmotion.imageutil.ImageConverter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScreenData {
    public Data data;
    public FFmpegSource ffs;
    public World world;

    public ScreenData(String name, FFmpegSource ffs, World world){
        // Please run with async
        data = new Data(name, ffs, world);
        setBackgroundColor(0,0,0);
        this.ffs = ffs;
        this.world = world;

        new Thread(this::loadFFS).start();
    }

    public ScreenData(File path) {
        try (FileInputStream f = new FileInputStream(path);
             ObjectInputStream in = new ObjectInputStream(f)) {

            data = (Data) in.readObject();

            VividMotion.screenDataList.add(this);
            if (!data.isPicture) {
                new VideoPlayer(this).start();
            }
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


        if (!data.map_pixel.isEmpty()){
            VividMotion.screenDataList.add(this);

            if (data.map_pixel.size() == 1){
                // isPicture
                data.isPicture = true;

                byte[][] pixelData = getMapData(0);
                for (int i = 0; i < data.mapIds.length; i++ ){
                    MapView view = Bukkit.getMap((short) data.mapIds[i]);
                    VividMotion.mapUtil.setColor(view, pixelData[i]);

                    for (MapRenderer render: view.getRenderers()){
                        view.removeRenderer(render);
                    }
                    view.addRenderer(new PictureRender(pixelData[i]));
                }
                data.map_pixel.clear();


            } else{
                // isVideo
                data.isPicture = false;

                byte[][] pixelData = getMapData(0);
                for (int i = 0; i < data.mapIds.length; i++ ){
                    MapView view = Bukkit.getMap((short) data.mapIds[i]);
                    VividMotion.mapUtil.setColor(view, pixelData[i]);

                    for (MapRenderer render: view.getRenderers()){
                        view.removeRenderer(render);
                    }
                }
                new VideoPlayer(this).start();
            }
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
        int m_height = data.mapHeight, m_width = data.mapWidth;

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
        data.background_color = (byte) ImageConverter.get_nearest_color(r,g,b);
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
        public int height, width, mapHeight, mapWidth;
        public double videoFrameRate, setFrameRate;
        public boolean is_loaded = false;
        // Byte[Frame][height*width]
        public final List<byte[][]> map_pixel = new ArrayList<>();
        public int[] mapIds;
        public byte background_color;
        public int nowFrame = -1;
        public boolean isPausing = false;
        public boolean isPicture;
        public String name;

        public Data(String name, FFmpegSource ffs, World world){
            this.name = name;
            height = ffs.height;
            width = ffs.width;
            videoFrameRate = ffs.frameRate;
            mapWidth = width % 128 == 0 ? width/128 : width/128+1;
            mapHeight = height % 128 == 0 ? height/128 : height/128+1;
            setFrameRate = 20.0;


            mapIds = new int[mapHeight * mapWidth];
            for (int i = 0; i < mapHeight * mapWidth; i++){
                MapView view = Bukkit.createMap(world);

                view.setCenterX(Integer.MAX_VALUE);
                view.setCenterZ(Integer.MAX_VALUE);
                view.setScale(MapView.Scale.FARTHEST);
                view.getRenderers().clear();

                mapIds[i] = view.getId();
            }
        }
    }
}
