package fox.ryukkun_;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class ParticleUtil_1_12_R1 implements ParticleUtil{
    @Override
    public void spawnParticle(World world, Location location, int r, int g, int b) {
        if (255 < r) r = 255;
        if (255 < g) g = 255;
        if (255 < b) b = 255;
        if (r < 1) r = 1;
        if (g < 0) g = 0;
        if (b < 0) b = 0;
        world.spawnParticle(Particle.REDSTONE, location, 0, r/255.0, g/255.0, b/255.0);
    }
}
