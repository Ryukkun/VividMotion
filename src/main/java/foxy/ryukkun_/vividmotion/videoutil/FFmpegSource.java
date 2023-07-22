package foxy.ryukkun_.vividmotion.videoutil;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class FFmpegSource {
    public boolean can_load = false;
    public int width, height;
    public double frameRate;
    public BufferedImage bimg = null;
    public FFmpegFrameGrabber ffg = null;
    private final Java2DFrameConverter java2d = new Java2DFrameConverter();

    public static Pattern re_url = Pattern.compile("https?://.+");






    public FFmpegSource(String path) {

        // is URL
        if (re_url.matcher(path).find()) {
            try {
                fromURL(new URL(path));
            }catch (MalformedURLException ignored){}

        // is File
        } else {
            File file = new File(path);
            if (file.exists()){
                fromFile( file);
            }
        }
    }

    public void fromURL(URL url) {
        try {
            bimg = ImageIO.read(url);
            if (bimg.getType() == BufferedImage.TYPE_3BYTE_BGR || bimg.getType() == BufferedImage.TYPE_4BYTE_ABGR){
                can_load = true;
            }

        } catch (IOException ignored) {}
        if (!can_load){
            bimg = null;
            ffg = new FFmpegFrameGrabber(url);
            can_load = ffmpeg_setup();
        }

        get_info();
    }

    public void fromFile(File file) {
        try {
            bimg = ImageIO.read(file);
            if (bimg.getType() == BufferedImage.TYPE_3BYTE_BGR || bimg.getType() == BufferedImage.TYPE_4BYTE_ABGR){
                can_load = true;
            }

        } catch (IOException ignored) {}
        if (!can_load){
            bimg = null;
            ffg = new FFmpegFrameGrabber(file);
            can_load = ffmpeg_setup();
        }

        get_info();
    }

    private boolean ffmpeg_setup() {
        try{
            ffg.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
            avutil.av_log_set_level(avutil.AV_LOG_QUIET);
            ffg.start();
            return true;

        }catch (FFmpegFrameGrabber.Exception e){
            ffg = null;
            return false;
        }
    }

    private void get_info(){
        if (can_load){
            if (bimg != null){
                width = bimg.getWidth();
                height = bimg.getHeight();
                frameRate = 0.0;

            } else if (ffg != null) {
                width = ffg.getImageWidth();
                height = ffg.getImageHeight();
                frameRate = ffg.getFrameRate();
            }
        }
    }

    public byte[] read() {
        if (can_load){
            if (bimg != null){
                return MapConverter.toConvert(bimg);

            } else {
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
                return MapConverter.toConvert( java2d.convert(f));

            }
        }
        return null;
    }
}
