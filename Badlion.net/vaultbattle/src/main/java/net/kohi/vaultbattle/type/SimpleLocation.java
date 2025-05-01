package net.kohi.vaultbattle.type;

import org.bukkit.Location;
import org.bukkit.World;

public class SimpleLocation {

    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public SimpleLocation(double x, double y, double z) {
        this(x, y, z, 0.0F, 0.0F);
    }

    public SimpleLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public SimpleLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SimpleLocation)) {
            return false;
        }
        SimpleLocation simpleLocation = (SimpleLocation) other;
        return this.x == simpleLocation.x
                && this.y == simpleLocation.y
                && this.z == simpleLocation.z
                && this.pitch == simpleLocation.pitch
                && this.yaw == simpleLocation.yaw;
    }

    @Override
    public int hashCode() {
        int hash1 = 19 + (int) (Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash1 = 19 * hash1 + (int) (Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash1 = 19 * hash1 + (int) (Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        hash1 = 19 * hash1 + Float.floatToIntBits(this.pitch);
        hash1 = 19 * hash1 + Float.floatToIntBits(this.yaw);
        return hash1;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
