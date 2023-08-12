package fox.ryukkun_;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class ParticleUtil_1_13_R2 implements ParticleUtil{
    @Override
    public void spawnParticle(World world, Location location, int r, int g, int b) {
        world.spawnParticle(Particle.REDSTONE, location, 0, new Particle.DustOptions(Color.fromRGB(r, g, b), 1));
    }
}
