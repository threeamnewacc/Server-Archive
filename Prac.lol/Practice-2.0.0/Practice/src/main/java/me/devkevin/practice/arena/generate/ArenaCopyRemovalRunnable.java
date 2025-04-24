package me.devkevin.practice.arena.generate;

import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.standalone.StandaloneArena;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright 31/12/2021 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ArenaCopyRemovalRunnable extends BukkitRunnable {

    private final int number;
    private final Arena arena;
    private final StandaloneArena arenaCopy;

    private final Practice plugin = Practice.getInstance();

    public ArenaCopyRemovalRunnable(int number, Arena arena, StandaloneArena arenaCopy) {
        this.number = number;
        this.arena = arena;
        this.arenaCopy = arenaCopy;
    }

    @Override
    public void run() {
        TaskManager.IMP.async(() -> {
            EditSession editSession = new EditSessionBuilder(arenaCopy.getA().getWorld()).fastmode(true).allowedRegionsEverywhere().autoQueue(false).limitUnlimited().build();
            CuboidRegion copyRegion = new CuboidRegion(
                    new Vector(arenaCopy.getMax().getX(), arenaCopy.getMax().getY(), arenaCopy.getMax().getZ()),
                    new Vector(arenaCopy.getMin().getX(), arenaCopy.getMin().getY(), arenaCopy.getMin().getZ())
            );

            try {
                editSession.setBlocks(copyRegion, new BaseBlock(BlockID.AIR));
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }

            editSession.flushQueue();
        });

        this.plugin.getMainConfig().getConfig().getConfigurationSection("arenas." + arena.getName() + ".standaloneArenas").set(String.valueOf(number), null);
        this.plugin.getMainConfig().save();

        this.plugin.getArenaManager().getArena(arena.getName()).getStandaloneArenas().remove(arenaCopy);
        this.plugin.getArenaManager().getArena(arena.getName()).getAvailableArenas().remove(number);
    }
}
