package fox.ryukkun_.vividmotion;

import fox.ryukkun_.ParticleUtil;
import fox.ryukkun_.ParticleUtil_1_12_R1;
import fox.ryukkun_.ParticleUtil_1_13_R1;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager {
    public static final boolean isOver_1_13 = (MCVersion.greaterThanEqual(MCVersion.v1_13_R1));

    public static final ParticleUtil particleUtil;
    static {
        if (isOver_1_13){
            particleUtil = new ParticleUtil_1_13_R1();
        } else{
            particleUtil = new ParticleUtil_1_12_R1();
        }
    }


    public static void spawnSquare(Location start, Location finish, Player player, Particle particle) {
        spawnSquare(start, finish, player, particle, 0, 0, 0);
    }


    public static void spawnSquare(Location start, Location finish, Player player, Particle particle, int r, int g, int b) {
        List<Location> locations = new ArrayList<>();

        locations.add(start);
        locations.add(finish);
        double xd = Math.abs(start.getX() - finish.getX());
        double yd = Math.abs(start.getY() - finish.getY());
        double zd = Math.abs(start.getZ() - finish.getZ());
        double vecS = 0.3;
        Location lCopy, lCopy2;
        if ( 0.5 < xd){
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
        if ( 0.5 < yd){
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
        if ( 0.5 < zd){
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

        if (particle.equals(Particle.REDSTONE)) {
            for (Location l : locations) {
                particleUtil.spawnParticle(player, l, r, g, b);
            }
        } else {
            for (Location l : locations) {
                player.spawnParticle(particle, l, 1);
            }
        }
    }
}
