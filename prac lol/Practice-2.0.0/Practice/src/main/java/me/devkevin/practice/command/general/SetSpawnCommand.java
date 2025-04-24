package me.devkevin.practice.command.general;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class SetSpawnCommand extends PracticeCommand {

    // CLASS RECODED 31/12/2021

    @Getter private static Practice plugin = Practice.getInstance();

    @Command(name = "setspawn", permission = Rank.DEVELOPER, inGameOnly = true, usage = "&cUsage: /setspawn <spawn|min|max|hololeaderboard>")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        switch (args[0]) {
            case "spawn":
                plugin.getCustomLocationManager().setSpawn(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Spawn has been set!");
                saveLocation(player, "spawn");
                break;
            case "min":
                plugin.getCustomLocationManager().setSpawnMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Spawn Min has been set!");
                saveLocation(player, "spawnMin");
                break;
            case "max":
                plugin.getCustomLocationManager().setSpawnMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Spawn Max has been set!");
                saveLocation(player, "spawnMax");
                break;
            case "hololeaderboard":
                plugin.getCustomLocationManager().setHoloLeaderboardsLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "You has set Spawn Holo Leaderboards location.\nSuccessfully saved spawn holo leaderboards location.");
                saveLocation(player, "hololeaderboard");
                break;
            case "npc1":
                plugin.getCustomLocationManager().setNpc1(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "You has set Spawn NPC 1 Leaderboards location.\nSuccessfully saved spawn holo leaderboards location.");
                saveLocation(player, "npc1");
                break;
            case "npc2":
                plugin.getCustomLocationManager().setNpc2(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "You has set Spawn NPC 2 Leaderboards location.\nSuccessfully saved spawn holo leaderboards location.");
                saveLocation(player, "npc2");
                break;
            case "npc3":
                plugin.getCustomLocationManager().setNpc3(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "You has set Spawn NPC 3 Leaderboards location.\nSuccessfully saved spawn holo leaderboards location.");
                saveLocation(player, "npc3");
                break;
            default:
                player.sendMessage(CC.translate(command.getCommand().getUsage()));
                break;
        }
    }

    private void saveLocation(Player player, String location) {
        FileConfiguration config = plugin.getMainConfig().getConfig();
        config.set(location, CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
        plugin.getMainConfig().save();
    }
}
