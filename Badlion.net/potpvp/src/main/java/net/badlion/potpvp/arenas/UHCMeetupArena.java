package net.badlion.potpvp.arenas;

import net.badlion.potpvp.managers.ArenaManager;
import org.bukkit.Location;
import org.bukkit.Material;

public class UHCMeetupArena extends BuildUHCArena {

    public UHCMeetupArena(String arenaName, Location warp1, Location warp2, String extraData) {
        super(arenaName, warp1, warp2, extraData);

        this.ceiling = Material.GLASS;
        this.walls = Material.WOOL;
    }

    @Override
    public void scan() {
        this.scanWithLocation(ArenaManager.getWarp(this.getArenaName() + "-1"));
    }
}
