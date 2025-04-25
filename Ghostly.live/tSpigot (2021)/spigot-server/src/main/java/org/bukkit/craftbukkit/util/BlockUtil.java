package org.bukkit.craftbukkit.util;

import org.bukkit.Location;

import net.minecraft.server.EntityFallingBlock;

public class BlockUtil {

	public static double fallingBlockTravelDistance(EntityFallingBlock entity){
        double distance = -1.0;
        double from [] = new double [2];
        double to [] = new double [2];

        Location spawn = entity.sourceLoc;
        from[0] = spawn.getX();
        from[1] = spawn.getZ();

        to[0] = entity.locX;
        to[1] = entity.locZ;
        distance = calculateDistance(from,to);

        return distance;
    }

    private static double calculateDistance(double[] from, double [] to){
        return Math.sqrt(((Math.pow((to[0]-from[0]),2) + Math.pow((to[1]-from[1]),2))));
    }
}
