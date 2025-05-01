package net.badlion.uhcworldgenerator.listeners;

import net.badlion.uhcworldgenerator.BorderGenerationCompleteEvent;
import net.badlion.uhcworldgenerator.UHCWorldCheckerTask;
import net.badlion.uhcworldgenerator.UHCWorldGenerator;
import net.badlion.worldborder.WorldFillerTaskCompleteEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.io.File;

public class UHCWorldListener implements Listener {

    @EventHandler
    public void onBorderGenerationCompleteEvent(BorderGenerationCompleteEvent event) {
        Bukkit.getLogger().info("Finished bedrock border: " + event.getWorldName());
        // Start generating the nether
        Bukkit.getLogger().info("Finished bedrock border for main world, generating nether now...");

        // Nether
        WorldCreator worldCreatorNether = new WorldCreator(UHCWorldCheckerTask.newWorldName + "_nether");
        worldCreatorNether.environment(World.Environment.NETHER);
        UHCWorldCheckerTask.worldNether = UHCWorldGenerator.plugin.getServer().createWorld(worldCreatorNether);

        UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
        UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + event.getWorldName() + "_nether set " + (UHCWorldCheckerTask.RADIUS_OF_CENTER / 2) + " 0 0");
        UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + event.getWorldName() + "_nether fill 1000");
        UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWorldFinishGeneration(WorldFillerTaskCompleteEvent event) {
        if (event.getWorldName().contains("end")) {
            File lock = new File(UHCWorldCheckerTask.folder, UHCWorldCheckerTask.newWorldName + "/gen.lock");
            if (lock.exists()) {
                Bukkit.getLogger().info("Deleting lock");
                lock.delete();
            }

            Bukkit.getLogger().info("Finished end, stopping now...");
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
        } else if (!event.getWorldName().contains("nether")) {
            Bukkit.getLogger().info("Finished world generation: " + event.getWorldName());

            // Setup bedrock border
            Bukkit.getLogger().info("Making worldedit border: " + event.getWorldName());
            UHCWorldCheckerTask.addBedrockBorder(event.getWorldName(), UHCWorldCheckerTask.RADIUS_OF_CENTER);
        } else {
            Bukkit.getLogger().info("Finished nether, generating end now...");

            // End
            WorldCreator worldCreatorEnd = new WorldCreator(UHCWorldCheckerTask.newWorldName + "_the_end");
            worldCreatorEnd.environment(World.Environment.THE_END);
            UHCWorldCheckerTask.worldEnd = UHCWorldGenerator.plugin.getServer().createWorld(worldCreatorEnd);

            UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
            UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + UHCWorldCheckerTask.world.getName() + "_the_end set 200 0 0");
            UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + UHCWorldCheckerTask.world.getName() + "_the_end fill 1000");
            UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");
        }
    }

}
