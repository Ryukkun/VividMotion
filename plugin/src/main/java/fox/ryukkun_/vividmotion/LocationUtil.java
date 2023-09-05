package fox.ryukkun_.vividmotion;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.util.Vector;


public class LocationUtil implements Cloneable{
    private Location location;
    private double roll;

    public LocationUtil(Location location, Rotation r) {
        this.location = location;
        if (r.equals(Rotation.NONE) || r.equals(Rotation.FLIPPED)) {
            roll = 0;
        } else if (r.equals( Rotation.CLOCKWISE_45) || r.equals( Rotation.FLIPPED_45)) {
            roll = -90;
        } else if (r.equals(Rotation.CLOCKWISE) || r.equals(Rotation.COUNTER_CLOCKWISE)) {
            roll = -180;
        } else if (r.equals(Rotation.CLOCKWISE_135) || r.equals(Rotation.COUNTER_CLOCKWISE_45)) {
            roll = 90;
        } else {
            Bukkit.getLogger().warning("Rotationがみつかんなかったわ result:"+r.name());
        }
    }

    public LocationUtil(Location location, double roll) {
        this.location = location;
        this.roll = roll;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public double getRoll() {
        return roll;
    }
    public void setRoll(double roll) {
        this.roll = roll;
    }


    public Location addLocalCoordinate(double x, double y, double z) {
        // calc roll
        double rollR = Math.toRadians(roll);
        double cosR = Math.cos(Math.toRadians(rollR));
        double sinR = Math.sin(rollR);

        double _x = x;
        x = _x*cosR + y*sinR;
        y = y*cosR + _x*sinR;

        Vector v1 = calcLocalCoordinate(location.getYaw(), location.getPitch(), z);
        Vector v2 = calcLocalCoordinate(location.getYaw(), location.getPitch()-90f, y);
        Vector v3 = calcLocalCoordinate(location.getYaw()-90f, 0, x);

        return location.add(v1).add(v2).add(v3).clone();
    }

    public static Vector calcLocalCoordinate(float yaw, float pitch, double vec) {
        double pitchR = Math.toRadians(-pitch);
        double yawR = Math.toRadians(yaw);
        double vec2D = vec * Math.cos(pitchR);

        double x = -vec2D * Math.sin(yawR);
        double y = vec * Math.sin(pitchR);
        double z = vec2D * Math.cos(yawR);

        //Bukkit.getLogger().info("yaw:"+yaw+" pitch:"+pitch+" vec:"+vec+" x:"+x+" y:"+y+" z:"+z);
        return new Vector(x, y, z);
    }

    @Override
    public LocationUtil clone() {
        try {
            LocationUtil clone = (LocationUtil) super.clone();
            clone.setLocation( getLocation().clone());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
