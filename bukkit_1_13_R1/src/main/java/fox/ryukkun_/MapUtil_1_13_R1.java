package fox.ryukkun_;

import org.bukkit.Bukkit;
import org.bukkit.map.MapView;


public class MapUtil_1_13_R1 implements MapGetter{
    @Override
    public MapView getMap(int id) {
        return Bukkit.getMap((short) id);
    }
}
