package foxy.ryukkun_.vividmotion.imageutil;

import org.bukkit.Bukkit;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.io.File;
import java.net.URL;
import java.util.regex.Pattern;


public class FFmpegSource {
    public boolean can_load = false;
    public int width, height;
    public double frameRate;
    public FFmpegFrameGrabber ffg = null;

    public static Pattern re_url = Pattern.compile("https?://.+");






    public FFmpegSource(String path) {

        // is URL
        if (re_url.matcher(path).find()) {
            try {
                URL url = new URL(path);
                fromURL(url);

            }catch (Exception ignored){
                close();
                can_load = false;
            }

        // is File
        } else {
            try {
                File file = new File(path);
                if (file.exists()){
                    fromFile( file);
                }
            } catch (Exception e) {
                close();
                can_load = false;
            }
        }
    }


    public void fromURL(URL url) {
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
            ffg.setPixelFormat(avutil.AV_PIX_FMT_BGR24);
            avutil.av_log_set_level(avutil.AV_LOG_QUIET);
            ffg.start();

            return true;

        }catch (FFmpegFrameGrabber.Exception e){
            Bukkit.getLogger().warning(e.toString());
            close();
            ffg = null;
            return false;
        }
    }


    public void close(){
        try {
            if (ffg != null){
                ffg.close();
                ffg = null;
            }
        } catch (FrameGrabber.Exception ignored) {}
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
            close();
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
            return ImageConverter.toConvert(f);

        }
        return null;
    }
}
