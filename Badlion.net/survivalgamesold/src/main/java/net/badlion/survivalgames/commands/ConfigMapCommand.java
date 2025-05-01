package net.badlion.survivalgames.commands;

import com.google.common.base.Joiner;
import io.nv.bukkit.CleanroomGenerator.CleanroomChunkGenerator;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.tasks.ChestFinderTask;
import net.badlion.worldrotator.GWorld;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigMapCommand implements CommandExecutor {

    private static Map<String, World> loadedWorlds = new HashMap<>();
    private static boolean chunksUnloading = false;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player player = (Player) sender;
        Location playerLocation = player.getLocation();
        String locString = "" + playerLocation.getX() + " " +  playerLocation.getY() + " " +  playerLocation.getZ() + " " +  playerLocation.getYaw() + " " +  playerLocation.getPitch();
        switch (args[0]) {
            case "loadworld":
                GWorld loadedWorld = SurvivalGames.getInstance().getWorldRotator().getGWorld(args[1]);
                if (loadedWorld != null && Bukkit.getWorld(loadedWorld.getNiceWorldName()) == null) {

                    WorldCreator wc = new WorldCreator(loadedWorld.getInternalName());
                    wc.generator(new CleanroomChunkGenerator("."));
                    World world = SurvivalGames.getInstance().getServer().createWorld(wc);
                    world.setAnimalSpawnLimit(0);
                    world.setMonsterSpawnLimit(0);
                    world.setTime(6000);
                    world.setGameRuleValue("doDaylightCycle", "false");
                    world.setGameRuleValue("doMobSpawning", "false");
                    world.setGameRuleValue("doTileDrops", "false");
                    world.getEntities().clear();
                    loadedWorld.setLoaded(true);
                    loadedWorlds.put(loadedWorld.getInternalName(), world);

                    sender.sendMessage(ChatColor.GREEN + args[1] + " has been loaded");
                } else {
                    sender.sendMessage("That is not a valid world name!");
                }
                break;
            case "author":
            case "authors":
                SurvivalGames.getInstance().getWorldRotator().getGWorld(player.getWorld().getName()).getYml().set("author", Joiner.on(" ").skipNulls().join(Arrays.copyOfRange(args, 1, args.length)));
                sender.sendMessage(ChatColor.GREEN + "Set Author");
                break;
            case "tp":
                GWorld targetWorld = SurvivalGames.getInstance().getWorldRotator().getGWorld(args[1]);
                if (loadedWorlds.containsKey(args[1])) {
                    player.teleport(new Location(loadedWorlds.get(args[1]), 0, 100, 0));
                    player.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.DARK_GREEN + args[1]);
                } else {
                    sender.sendMessage("That is not a valid world name!");
                }
                break;
            case "findchests":
                World currentWorld = player.getWorld();
                Chunk[] loadedChunks = currentWorld.getLoadedChunks(); // NOTE: We are assuming the map's chunks have been loaded correctly, so don't let BurntCactus do this set-up
                ChestFinderTask chestFinderTask = new ChestFinderTask(loadedChunks, SurvivalGames.getInstance().getWorldRotator().getGWorld(currentWorld.getName()));
                chestFinderTask.runTask(SurvivalGames.getInstance());
                sender.sendMessage(ChatColor.GREEN + "Chests are being looked for...");
                break;
            case "addspawn":
                List<String> spawnLocations = SurvivalGames.getInstance().getWorldRotator().getGWorld(player.getWorld().getName()).getYml().getStringList("spawn_locations");
                spawnLocations.add(locString);
                SurvivalGames.getInstance().getWorldRotator().getGWorld(player.getWorld().getName()).getYml().set("spawn_locations", spawnLocations);
                sender.sendMessage(ChatColor.GREEN + "Spawn Location saved.");
                break;
            case "adddeathspawn":
                List<String> deathMatchLocations = SurvivalGames.getInstance().getWorldRotator().getGWorld(player.getWorld().getName()).getYml().getStringList("deathmatch_locations");
                deathMatchLocations.add(locString);
                SurvivalGames.getInstance().getWorldRotator().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_locations", deathMatchLocations);
                sender.sendMessage(ChatColor.GREEN + "DeathMatch Location saved.");
                break;
            case "saveconfig":
                try {
                    SurvivalGames.getInstance().getWorldRotator().getGWorld(player.getWorld().getName()).save();
                    sender.sendMessage(ChatColor.GREEN + "Config file has been saved.");
                } catch (IOException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.DARK_RED + "SOMETHING TERRIBLE HAS HAPPENED: FILE SAVE HAS FAILED");
                }
                break;
            case "spectator_deathmatch_location":
                SurvivalGames.getInstance().getWorldRotator().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_spectator_location", locString);
                sender.sendMessage(ChatColor.GREEN + "Set Spectator Deathmatch Location");
                break;
            case "deathmatch_arena":
                SurvivalGames.getInstance().getWorldRotator().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_arena", Boolean.parseBoolean(args[1]));
                sender.sendMessage(ChatColor.GREEN + "Set Deathmatch arena");
                break;
            case "deathmatch_radius":
                SurvivalGames.getInstance().getWorldRotator().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_radius", Integer.parseInt(args[1]));
                sender.sendMessage(ChatColor.GREEN + "Set Deathmatch radius");
                break;
            case "deathmatch_center":
                SurvivalGames.getInstance().getWorldRotator().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_center", locString);
                sender.sendMessage(ChatColor.GREEN + "Set Deathmatch center");
                break;
        }

        return true;
    }


    public static boolean isChunksUnloading() {
        return chunksUnloading;
    }

    public static void setChunksUnloading(boolean chunksUnloading) {
        ConfigMapCommand.chunksUnloading = chunksUnloading;
    }
}


