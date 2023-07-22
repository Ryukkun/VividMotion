package foxy.ryukkun_.vividmotion.videoutil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import net.minecraft.server.v1_12_R1.Entity;

public class GetMap extends Thread{
    public FFmpegSource ffs;
    public Player player;

    public GetMap(FFmpegSource ffs, Player player){
        this.ffs = ffs;
        this.player = player;
    }
    @Override
    public void run() {
        MapsData mData = new MapsData(ffs.width, ffs.height, ffs.frameRate);
        while (true){
            byte[] frame = ffs.read();
            if (frame == null){
                break;
            }
            mData.addFrame(frame);
        }

        MapView view;
        byte[][] maps_pixel = mData.getMapDatas(0);
        for (int i = 0; i < maps_pixel.length; i++){
            view = Bukkit.createMap( player.getWorld());
            CraftPlayer
        }
        Bukkit.createMap( player.getWorld());
    }
}
