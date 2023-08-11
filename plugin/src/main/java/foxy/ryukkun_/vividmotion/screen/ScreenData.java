package foxy.ryukkun_.vividmotion.screen;

import foxy.ryukkun_.vividmotion.VividMotion;
import foxy.ryukkun_.vividmotion.imageutil.FFmpegSource;
import foxy.ryukkun_.vividmotion.imageutil.ImageConverter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


public class ScreenData {
    public static int FILE_FRAME = 50;
    public Data data;
    public FFmpegSource ffs;
    private SnappyInputStream inputStream = null;
    public boolean loopEnable = true;

    public ScreenData(String name, FFmpegSource ffs, World world){
        data = new Data(name, ffs, world);
        setBackgroundColor(0,0,0);
        this.ffs = ffs;

        VividMotion.screenDataList.add(this);
        new Thread(this::loadFFS).start();
    }



    public ScreenData(File path) {
        try (FileInputStream f = new FileInputStream(path);
             ObjectInputStream in = new ObjectInputStream(f)) {

            data = (Data) in.readObject();

            VividMotion.screenDataList.add(this);
            if (!data.isPicture) {

                for (int i : data.mapIds){
                    MapView view = VividMotion.mapGetter.getMap( i);
                    for (MapRenderer renderer : view.getRenderers()){
                        view.removeRenderer( renderer);
                    }
                }
                MapView view = VividMotion.mapGetter.getMap( data.mapIds[0]);
                view.addRenderer(new VideoPlayer.MapDetector());
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
        VideoPlayer.MapDetector mapDetector = new VideoPlayer.MapDetector();
        VividMotion.mapGetter.getMap( data.mapIds[0])
                .addRenderer( mapDetector);

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
                    out = new SnappyOutputStream(
                            Files.newOutputStream(
                                    getFile( frameCount / FILE_FRAME)));
                }
                for (byte[] b_ : b ){
                    out.write(b_);
                }
                frameCount++;

                // Send Map
                long nowTime = System.currentTimeMillis();
                if (1000 < nowTime - lastSend){

                    MapPacketSender.sendPixelData(this, b);
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
            VividMotion.mapGetter.getMap( data.mapIds[0]).removeRenderer( mapDetector);
            for (int i = 0; i < data.mapIds.length; i++ ){
                MapView view = VividMotion.mapGetter.getMap( data.mapIds[i]);
                VividMotion.mapUtil.setColor(view, pixelData[i]);

                view.addRenderer(new PictureRender(pixelData[i]));
            }

            File file = getFile(0).toFile();
            if (file.exists()){
                boolean ignored = file.delete();
            }


        } else{
            // isVideo
            data.isPicture = false;

            byte[][] pixelData = getMapData(0);
            for (int i = 0; i < data.mapIds.length; i++ ){
                MapView view = VividMotion.mapGetter.getMap( (short) data.mapIds[i]);
                VividMotion.mapUtil.setColor(view, pixelData[i]);

            }
            new VideoPlayer(this).start();
        }
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




    public File getFile(){
        Path path = VividMotion.getMapDataFolder().toPath();
        path = path.resolve(data.name);
        File file = path.toFile();
        boolean ignored = file.mkdirs();
        return file;
    }

    public Path getFile(int fileNum) {
        return getFile().toPath().resolve(fileNum+".mapmeta");
    }


    public void delete(){
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            this.loopEnable = false;
            VividMotion.screenDataList.remove( this);
            FileUtils.deleteDirectory( getFile());

        } catch (Exception e){
            Bukkit.getLogger().info(e.getMessage());
        }
    }


    public boolean setFrameRate(double fr){
        if (0.0 < fr && fr <= data.videoFrameRate){
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



    public void setBackgroundColor(int r, int g, int b){
        data.background_color = (byte) ImageConverter.get_nearest_color(r,g,b);
    }

    public byte[][] getMapData(){
        return getMapData(data.nowFrame + data.videoFrameRate / getFrameRate());
    }


    public byte[][] getMapData(double frame_b){
        int frame = (int)frame_b;

        try {
            if (data.frameCount <= frame){
                frame = 0;
                frame_b -= data.frameCount;
            }

            int frameLength = 128*128*data.mapIds.length;
            int fileNum = frame / FILE_FRAME;
            int frameInFile = frame % FILE_FRAME;
            int nowFrame = (int)data.nowFrame;


            if (nowFrame / FILE_FRAME != frame / FILE_FRAME || inputStream == null){
                // 新しく InputStreamを作成

                inputStream = new SnappyInputStream(
                        Files.newInputStream(
                                getFile( fileNum)));

                if (frameInFile != 0){
                    inputStream.skip((long) frameLength *frameInFile);
                }



            } else if (nowFrame+1 != frame) {
                // 既存のInputStreamを再利用

                if (nowFrame+1 < frame) {
                    inputStream.skip((long) (frame - nowFrame - 1) *frameLength);

                } else{
                    inputStream = new SnappyInputStream(
                            Files.newInputStream(
                                    getFile( fileNum)));

                    if (frameInFile != 0){
                        inputStream.skip((long) frameLength *frameInFile);
                    }
                }
            }


            // get byte[][]
            byte[][] res = new byte[data.mapIds.length][128*128];

            for (int i = 0; i < data.mapIds.length; i++){
                byte[] _res = new byte[128*128];
                if (inputStream.read(_res) == -1){
                    return null;
                }
                res[i] = _res;
            }

            data.nowFrame = frame_b;
            return res;

        } catch (IOException e) {
            for (StackTraceElement s : e.getStackTrace()){
                Bukkit.getLogger().info(s.toString());
            }
            return null;
        }
    }




    public static class Data implements Serializable {
        // Map Count (Height, Width)
        public int height, width, mapHeight, mapWidth;
        public double videoFrameRate, setFrameRate;
        public boolean is_loaded = false;
        public int[] mapIds;
        public byte background_color;
        public double nowFrame = 0;
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
                for (MapRenderer render: view.getRenderers()){
                    view.removeRenderer(render);
                }

                mapIds[i] = view.getId();
            }
        }
    }
}
