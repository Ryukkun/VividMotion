package foxy.ryukkun_.vividmotion.videoutil;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
public class FFmpegSource extends Thread {
    public FFmpegSource(String file_path) throws FFmpegFrameGrabber.Exception {
        FFmpegFrameGrabber ffg = new FFmpegFrameGrabber(file_path);
        ffg.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
        ffg.start();
    }

    public void run(){

    }
}
