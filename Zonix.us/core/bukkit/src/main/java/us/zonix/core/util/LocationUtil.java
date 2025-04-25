package us.zonix.core.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LocationUtil {

    public static String parseLocation(Location location) {
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
    }

    public static Location convertLocation(String string) {
        String[] build = string.split(":");
        World world = Bukkit.getWorld(build[0]);
        double x = Double.parseDouble(build[1]);
        double y = Double.parseDouble(build[2]);
        double z = Double.parseDouble(build[3]);
        float yaw = Float.parseFloat(build[4]);
        float pitch = Float.parseFloat(build[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Location getHighestLocation(final Location origin) {
        return getHighestLocation(origin, null);
    }

    public static Location getHighestLocation(final Location origin, final Location def) {
        Preconditions.checkNotNull((Object) origin, (Object) "The location cannot be null");
        final Location cloned = origin.clone();
        final World world = cloned.getWorld();
        final int x = cloned.getBlockX();
        int y = world.getMaxHeight();
        final int z = cloned.getBlockZ();
        while (y > origin.getBlockY()) {
            final Block block = world.getBlockAt(x, --y, z);
            if (!block.isEmpty()) {
                final Location next = block.getLocation();
                next.setPitch(origin.getPitch());
                next.setYaw(origin.getYaw());
                return next;
            }
        }
        return def;
    }

    public static void multiplyVelocity(Player player, Vector vector, double multiply, double addY) {
        vector.normalize();
        vector.multiply(multiply);
        vector.setY(vector.getY() + addY);
        player.setFallDistance(0.0F);

        player.setVelocity(vector);
    }

}
