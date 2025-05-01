package net.badlion.gfactions.managers;

import net.badlion.gfactions.ClaimedChunk;
import net.badlion.gfactions.Faction;
import net.badlion.gfactions.util.FlatMap;
import org.bukkit.Location;

public class ClaimManager {

    public static int MAP_RADIUS_IN_BLOCKS = 5000;
    private static FlatMap<ClaimedChunk> chunkMap;

    public static void initialize() {
        // Leave some buffer
        int numOfChunksRequired = (ClaimManager.MAP_RADIUS_IN_BLOCKS + 300) >> 4;
        ClaimManager.chunkMap = new FlatMap<>(numOfChunksRequired); // add the
    }

    public Faction getFactionAtLand(Location location) {
        ClaimedChunk claimedChunk = ClaimManager.chunkMap.get(location.getChunk().getX(), location.getChunk().getZ());
        if (claimedChunk == null) {
            return null;
        }

        return claimedChunk.getFactionBlock(location);
    }

}
