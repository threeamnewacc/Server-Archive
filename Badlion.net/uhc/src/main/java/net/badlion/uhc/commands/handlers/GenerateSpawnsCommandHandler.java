package net.badlion.uhc.commands.handlers;

import net.badlion.common.Configurator;
import net.badlion.gberry.utils.Pair;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.util.ScatterUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GenerateSpawnsCommandHandler {

    private static LinkedList<Pair<Integer, Integer>> chunksToLoad = new LinkedList<>();
    public static ArrayList<Location> scatterPoints;

    public static void handleGenerateSpawnsCommand(final CommandSender sender, String[] args) {
        List<Configurator.Option> options = BadlionUHC.getInstance().getConfigurator().unconfiguredOptions();

        if (options.size() > 0) {
            sender.sendMessage(ChatColor.RED + "The following options need to be set.");
            for (Configurator.Option option : options) {
                sender.sendMessage(option.toString());
            }
            return;
        }

        int param = (Integer) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.MAXPLAYERS.name()).getValue();

        sender.sendMessage(ChatColor.YELLOW + "Server is currently loading spawn points for " + param + " players to warp to on UHC start. Please wait until this completes before starting.");
        BadlionUHC.getInstance().setState(BadlionUHC.BadlionUHCState.SPAWN_GENERATION);

        // Generate all of our locations
        GenerateSpawnsCommandHandler.scatterPoints = ScatterUtils.randomSquareScatter(param);

        World w = Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME);
        GenerateSpawnsCommandHandler.scatterPoints.add(new Location(w, 0, w.getHighestBlockYAt(0, 0) + 10, 0)); // Add scatter point for host

        final World world = Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME);
        for (int i = 0; i < param; i++) {
            // Get info for chunk that is going to spawn the player at
            Location location = scatterPoints.get(i);
            int x = location.getChunk().getX();
            int z = location.getChunk().getZ();

            // Add chunks around it for loading
            for (int j = x - 3; j < x + 3; j++) {
                for (int k = z - 3; k < z + 3; k++) {
                    GenerateSpawnsCommandHandler.chunksToLoad.add(Pair.of(j, k));
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                // Some weird race condition
                if (GenerateSpawnsCommandHandler.chunksToLoad == null) {
                    this.cancel();
                    return;
                }

                if (GenerateSpawnsCommandHandler.chunksToLoad.size() == 0) {
                    this.cancel();
                    sender.sendMessage(ChatColor.GREEN + "Finished pre-loading spawn points.");
                    GenerateSpawnsCommandHandler.chunksToLoad = null;
                    BadlionUHC.getInstance().setState(BadlionUHC.BadlionUHCState.PRE_START);

                    return;
                }

                // Go through and load 5 chunks on each tick until we run out of shit to load
                for (int i = 0; i < 5; i++) {
                    if (GenerateSpawnsCommandHandler.chunksToLoad.size() != 0) {
                        Pair<Integer, Integer> pair = GenerateSpawnsCommandHandler.chunksToLoad.remove();
                        world.loadChunk(pair.getA(), pair.getB());
                    }
                }
            }
        }.runTaskTimer(BadlionUHC.getInstance(), 0, 1);

    }

    public static Location getNewLocation() {
        return ScatterUtils.randomSquareScatter(1).get(0);
    }

    public static Location getNewLocation(int radius) {
        return ScatterUtils.randomSquareScatter(1, radius, 5).get(0);
    }

}
