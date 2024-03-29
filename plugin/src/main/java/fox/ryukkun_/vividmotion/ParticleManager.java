package fox.ryukkun_.vividmotion;

import fox.ryukkun_.ParticleUtil;
import fox.ryukkun_.ParticleUtil_1_12_R1;
import fox.ryukkun_.ParticleUtil_1_13_R1;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager {
//    public static final ParticleUtil particleUtil =
//            MCVersion.greaterThanEqual(MCVersion.v1_13_R1) ?
//            new ParticleUtil_1_13_R1() :
//            new ParticleUtil_1_12_R1();
    public static final ParticleUtil particleUtil = new Particle1_13_R1();


    public static void spawnSquare(Location start, Location finish, Player player, Particle particle) {
        spawnSquare(start, finish, player, particle, 0, 0, 0);
    }


    public static void spawnSquare(Location start, Location finish, Player player, Particle particle, int r, int g, int b) {
        List<Location> locations = new ArrayList<>();

        locations.add(start);
        locations.add(finish);

        double vecS = 0.3;
        double xd = Math.abs(start.getX() - finish.getX()) - vecS;
        double yd = Math.abs(start.getY() - finish.getY()) - vecS;
        double zd = Math.abs(start.getZ() - finish.getZ()) - vecS;
        Location lCopy, lCopy2;
        if ( 0.0 < xd){
            lCopy = start.clone();
            lCopy2 = finish.clone();
            double v = (start.getX() - finish.getX()) < 0 ? vecS : -vecS;
            for (double d = 0.0; d < xd; d+=vecS) {
                lCopy.add(v, 0, 0);
                lCopy2.add(-v, 0, 0);

                locations.add(lCopy.clone());
                locations.add(lCopy2.clone());
            }
        }
        if ( 0.0 < yd){
            lCopy = start.clone();
            lCopy2 = finish.clone();
            double v = (start.getY() - finish.getY()) < 0 ? vecS : -vecS;
            for (double d = 0.0; d < yd; d+=vecS) {
                lCopy.add(0, v, 0);
                lCopy2.add(0, -v, 0);

                locations.add(lCopy.clone());
                locations.add(lCopy2.clone());
            }
        }
        if ( 0.0 < zd){
            lCopy = start.clone();
            lCopy2 = finish.clone();
            double v = (start.getZ() - finish.getZ()) < 0 ? vecS : -vecS;
            for (double d = 0.0; d < zd; d+=vecS) {
                lCopy.add(0, 0, v);
                lCopy2.add(0, 0, -v);

                locations.add(lCopy.clone());
                locations.add(lCopy2.clone());
            }
        }

        try {
            if (particle.equals(Particle.REDSTONE)) {
                for (Location l : locations) {
                    particleUtil.spawnParticle(player, l, r, g, b);
                }
            } else {
                for (Location l : locations) {
                    player.spawnParticle(particle, l, 1, 0, 0, 0, 0);
                }
            }
        } catch (Exception e){
            Bukkit.broadcastMessage(e.toString());
        }
    }
}
