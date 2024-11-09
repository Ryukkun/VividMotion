package fox.ryukkun_.vividmotion;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Getter
public enum MCVersion {
    v1_12_R1(1,12,1),
    v1_13_R1(1,13,1),
    v1_13_R2(1,13,2),
    v1_14_R1(1,14,1),
    v1_15_R1(1,15,1),
    v1_16_R1(1,16,1),
    v1_16_R2(1,16,2),
    v1_16_R3(1,16,3),
    v1_17_R1(1,17,1),
    v1_18_R1(1,18,1),
    v1_18_R2(1,18,2),
    v1_19_R1(1,19,1),
    v1_19_R2(1,19,2),
    v1_19_R3(1,19,3),
    v1_20_0(1,20,0), // 1.20から表記変更
    v1_20_1(1,20,1), // craftBukkitのバージョンから serverのバージョンへ
    v1_20_2(1,20,2),
    v1_20_3(1,20,3),
    v1_20_4(1,20,4),
    v1_20_5(1,20,5),
    v1_20_6(1,20,6),
    v1_21_0(1,21,0),
    v1_21_1(1,21,1),
    v1_21_2(1,21,2),
    v1_21_3(1,21,3),
    v1_21_4(1,21,4),
    unknown(100, 100, 100);

    private final static MCVersion ver;
    private final int major, minor, patch, num;
    static {
        ver = init();
    }
    private static MCVersion init() {
        Logger log = VividMotion.plugin.getLogger();
        // craftBukkit version
        final String[] craftBukkitVer = Bukkit.getServer().getClass().getPackage().getName().split("\\."); //[3]
        MCVersion ret = (craftBukkitVer.length >= 4) ? nameOf(craftBukkitVer[3]) : unknown;

        try {
            if (ret.equals(unknown)) {
                // server version
                final String serverVer = Bukkit.getServer().getBukkitVersion().split("-")[0];
                final List<Integer> serverVerNum = Arrays.stream(serverVer.split("\\.")).map(Integer::valueOf).collect(Collectors.toList());
                if (serverVerNum.size() <= 2) {
                    serverVerNum.add(0);
                }
                ret = nameOf(serverVerNum.get(0), serverVerNum.get(1), serverVerNum.get(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ret.equals(unknown)) {
            log.info("サポートされていないバージョンです。正しく動かない可能性があります。");
        } else {
            log.info("minecraft : " + ret.name() +" が検出されました。=)");
        }

        return ret;
    }

    MCVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.num = major*10000 + minor*100 + patch;
    }

    public static MCVersion nameOf(String name) {
        for (MCVersion v : values()) {
            if (v.name().equalsIgnoreCase( name.trim())) {
                return v;
            }
        }
        return unknown;
    }

    public static MCVersion nameOf(int major, int minor, int patch) {
        for (MCVersion v : values()) {
            if (v.major == major && v.minor == minor && v.patch == patch) {
                return v;
            }
        }
        return unknown;
    }

    public static boolean lessThanEqual(MCVersion target) {
        return ver.num <= target.num;
    }

    public static boolean greaterThanEqual(MCVersion target) {
        return target.num <= ver.num;
    }

    public static boolean equal(MCVersion target) {
        return ver.minor == target.num;
    }

    public static String getNMS() {
        return "net.minecraft.server."+versionString+".";
    }

    public static String getCB() {
        return "org.bukkit.craftbukkit."+versionString+".";
    }
}
