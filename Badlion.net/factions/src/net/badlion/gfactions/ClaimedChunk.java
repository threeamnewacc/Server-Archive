package net.badlion.gfactions;

import org.bukkit.Location;

public class ClaimedChunk {

    private Faction[][] factionBlocks = new Faction[16][16];

    public ClaimedChunk() {
        // Handle DB initialization
    }

    public Faction getFactionBlock(Location location) {
        return this.factionBlocks[location.getBlockX() % 16][location.getBlockZ() % 16];
    }

    public void setFactionBlock(Location location, Faction faction) {
        this.factionBlocks[location.getBlockX() % 16][location.getBlockZ() % 16] = faction;
    }

}
