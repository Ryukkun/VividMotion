package fox.ryukkun_.vividmotion;

import org.bukkit.Bukkit;

public enum MCVersion {
    v1_12_R1(1121, "v1_12_R1"),
    v1_13_R1(1131, "v1_13_R1"),
    v1_13_R2(1132, "v1_13_R2"),
    v1_14_R1(1141, "v1_14_R1"),
    v1_15_R1(1151, "v1_15_R1"),
    v1_16_R1(1161, "v1_16_R1"),
    v1_16_R2(1162, "v1_16_R2"),
    v1_16_R3(1163, "v1_16_R3"),
    v1_17_R1(1171, "v1_17_R1"),
    v1_18_R1(1181, "v1_18_R1"),
    v1_18_R2(1182, "v1_18_R2"),
    v1_19_R1(1191, "v1_19_R1"),
    v1_19_R2(1192, "v1_19_R2"),
    v1_19_R3(1193, "v1_19_R3"),
    v1_20_R1(1201, "v1_20_R1"),
    v1_20_R2(1202, "v1_20_R2"),
    v1_20_R3(1203, "v1_20_R3"),
    unknown(5000, "");

    public final static MCVersion version;
    public final static String versionString;
    static {
        String v = Bukkit.getServer().getClass().getPackage().getName();
        versionString = v.substring( v.lastIndexOf('.') + 1);
        version = MCVersion.nameOf( versionString);
    }

    public final int num;
    public final String name;
    MCVersion(int i, String name) {
        num = i;
        this.name = name;
    }

    public static MCVersion nameOf(String name) {
        for (MCVersion v : values()) {
            if (v.name.equalsIgnoreCase( name.trim())) {
                return v;
            }
        }
        return unknown;
    }

    public static boolean lessThanEqual(MCVersion version) {
        return MCVersion.version.num <= version.num;
    }

    public static boolean greaterThanEqual(MCVersion version) {
        return version.num <= MCVersion.version.num;
    }

    public static boolean equal(MCVersion version) {
        return MCVersion.version.num == version.num;
    }

    public static String getNMS() {
        return "net.minecraft.server."+versionString+".";
    }

    public static String getCB() {
        return "org.bukkit.craftbukkit."+versionString+".";
    }
}
