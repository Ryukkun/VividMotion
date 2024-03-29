package fox.ryukkun_.vividmotion;

import fox.ryukkun_.ParticleUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Particle1_13_R1 implements ParticleUtil {
    @Override
    public void spawnParticle(Player player, Location location, int r, int g, int b) throws Exception{
        player.spawnParticle(Particle.REDSTONE, location, 0, Class.forName("org.bukkit.Particle.DustOptions").getConstructor(Color.class, int.class).newInstance(Color.fromRGB(r, g, b), 1));
    }
}
