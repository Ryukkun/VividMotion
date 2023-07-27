package foxy.ryukkun_.vividmotion.videoutil;

import org.bukkit.Bukkit;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class FFmpegSource {
    public boolean can_load = false;
    public int width, height;
    public double frameRate;
    public FFmpegFrameGrabber ffg = null;
    private final Java2DFrameConverter java2d = new Java2DFrameConverter();

    public static Pattern re_url = Pattern.compile("https?://.+");






    public FFmpegSource(String path) {

        // is URL
        if (re_url.matcher(path).find()) {
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();
                InputStream input = connection.getInputStream();
                fromURL(input);
            }catch (Exception ignored){}

        // is File
        } else {
            File file = new File(path);
            if (file.exists()){
                fromFile( file);
            }
        }
    }

    public void fromURL(InputStream url) {
        ffg = new FFmpegFrameGrabber(url);
        can_load = ffmpeg_setup();

        get_info();
    }

    public void fromFile(File file) {
        ffg = new FFmpegFrameGrabber(file);
        can_load = ffmpeg_setup();

        get_info();
    }

    private boolean ffmpeg_setup() {
        try{
            ffg.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
            avutil.av_log_set_level(avutil.AV_LOG_QUIET);
            ffg.start();
            return true;

        }catch (FFmpegFrameGrabber.Exception e){
            Bukkit.getLogger().warning(e.toString());
            ffg = null;
            return false;
        }
    }

    private void get_info(){
        if (can_load && ffg != null){
            width = ffg.getImageWidth();
            height = ffg.getImageHeight();
            frameRate = ffg.getFrameRate();
        }
    }


    public byte[] read() {
        byte[] res = _read();
        if (res == null) {
            try {
                ffg.close();
                ffg.release();
            } catch (FrameGrabber.Exception ignored) {}
        }
        return res;
    }


    public byte[] _read() {
        if (can_load && ffg != null){
            Frame f;
            try{
                f = ffg.grabImage();
            } catch (FFmpegFrameGrabber.Exception e){
                return null;
            }
            if (f == null){
                return null;
            } else if (f.image == null) {
                return null;
            }
            return MapConverter.toConvert(f);

        }
        return null;
    }
}
