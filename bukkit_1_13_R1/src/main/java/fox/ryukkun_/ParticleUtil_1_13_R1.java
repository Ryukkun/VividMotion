package fox.ryukkun_;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleUtil_1_13_R1 implements ParticleUtil{
    @Override
    public void spawnParticle(Player player, Location location, int r, int g, int b) {
         player.spawnParticle(Particle.REDSTONE, location, 0, new Particle.DustOptions(Color.fromRGB(r, g, b), 1));
    }
}
