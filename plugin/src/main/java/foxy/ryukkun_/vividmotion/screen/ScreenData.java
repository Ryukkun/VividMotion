package foxy.ryukkun_.vividmotion.screen;

import fox.ryukkun_.MapPacket;
import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.imageutil.FFmpegSource;
import foxy.ryukkun_.vividmotion.imageutil.ImageConverter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScreenData {
    public static int FILE_FRAME = 50;
    public Data data;
    public FFmpegSource ffs;
    public Player player;

    public ScreenData(String name, FFmpegSource ffs, Player player){
        data = new Data(name, ffs, player.getWorld());
        setBackgroundColor(0,0,0);
        this.ffs = ffs;
        this.player = player;

        VividMotion.screenDataList.add(this);
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

    public void loadFFS() {
        // Please run with async
        long lastSend = 0;
        byte[] frame;
        int frameCount = 0;
        SnappyOutputStream out = null;
        File file = null;
        try {
            while (true){
                frame = ffs.read();
                if (frame == null){
                    break;
                }
                // Save MapPixel
                byte[][] b = toMapFormat(frame);
                if (frameCount % FILE_FRAME == 0){
                    if (out != null) {
                        out.close();
                    }
                    file = getFile().toPath().resolve((frameCount / FILE_FRAME)+".pdat").toFile();
                    FileOutputStream f = new FileOutputStream( file);
                    out = new SnappyOutputStream( new BufferedOutputStream( f));
                }
                for (byte[] b_ : b ){
                    out.write(b_);
                }
                frameCount++;

                // Send Map
                long nowTime = System.currentTimeMillis();
                if (1000 < nowTime - lastSend){
                    List<MapPacket> packetList = new ArrayList<>();

                    for (int i = 0; i < data.mapIds.length; i++) {
                        packetList.add(new MapPacket(data.mapIds[i], b[i]));
                    }

                    VividMotion.packetManager.sendPacket(player, packetList);
                    lastSend = nowTime;
                }
            }

            // Loaded
            if (out != null) {
                out.close();
            }
        } catch (IOException e){
            Bukkit.getLogger().warning(e.getMessage());
        }


        // Loaded
        data.frameCount = frameCount;
        data.is_loaded = true;


        if (frameCount == 1){
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

            if (file.exists()){
                boolean ignored = file.delete();
            }


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


    public File getFile(){
        Path path = VividMotion.getMapDataFolder().toPath();
        path = path.resolve(data.name);
        File file = path.toFile();
        boolean ignored = file.mkdirs();
        return file;
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



    public byte[][] toMapFormat(byte[] bytes) {
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
        return maps;
    }

    public void setBackgroundColor(int r, int g, int b){
        data.background_color = (byte) ImageConverter.get_nearest_color(r,g,b);
    }

    public byte[][] getMapData(){
        data.nowFrame++;
        return getMapData(data.nowFrame);
    }


    public byte[][] getMapData(int frame){
        if (data.frameCount <= frame){
            frame = 0;
        }
        data.nowFrame = frame;

        File file = getFile().toPath().resolve((frame / FILE_FRAME)+".pdat").toFile();
        if (!file.exists()) {
            return null;
        }

        byte[][] res = new byte[data.mapIds.length][128*128];
        try (FileInputStream f = new FileInputStream( file);
            SnappyInputStream in = new SnappyInputStream( new BufferedInputStream( f))){

            in.mark(FILE_FRAME*128*128);
            for (int i = 0; i < data.mapIds.length; i++){
                byte[] _res = new byte[128*128];
                if (in.read(_res) == -1){
                    return null;
                }
                res[i] = _res;
            }
            } catch (IOException e) {
            return null;
        }

        return res;
    }




    public static class Data implements Serializable {
        // Map Count (Height, Width)
        public int height, width, mapHeight, mapWidth;
        public double videoFrameRate, setFrameRate;
        public boolean is_loaded = false;
        public int[] mapIds;
        public byte background_color;
        public int nowFrame = -1;
        public boolean isPausing = false;
        public boolean isPicture;
        public String name;
        public int frameCount = 0;

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
