package me.devkevin.practice.command.general;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 22/08/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class SetEventSpawnCommand extends PracticeCommand {

    @Command(name = "event.setspawn", permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "/event setspawn <subCommands>");
            player.sendMessage("");
            player.sendMessage(CC.YELLOW + CC.BOLD + "Sub Commands");
            player.sendMessage("");
            player.sendMessage(CC.GRAY + CC.BOLD + "sumolocation");
            player.sendMessage(CC.GRAY + CC.BOLD + "sumofirst");
            player.sendMessage(CC.GRAY + CC.BOLD + "sumosecond");
            player.sendMessage(CC.GRAY + CC.BOLD + "sumomin");
            player.sendMessage(CC.GRAY + CC.BOLD + "sumomax");
            player.sendMessage("");
        }

        switch (args[0].toLowerCase()) {
            case "sumolocation":
                Practice.getInstance().getCustomLocationManager().setSumoLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the sumo location.");
                break;
            case "sumofirst":
                Practice.getInstance().getCustomLocationManager().setSumoFirst(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the sumo location A.");
                break;
            case "sumosecond":
                Practice.getInstance().getCustomLocationManager().setSumoSecond(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the sumo location B.");
                break;
            case "sumomin":
                Practice.getInstance().getCustomLocationManager().setSumoMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the sumo min.");
                break;
            case "sumomax":
                Practice.getInstance().getCustomLocationManager().setSumoMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the sumo max.");
                break;

            case "taglocation":
                Practice.getInstance().getCustomLocationManager().setTnttagLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the TNTTag location.");
                break;
            case "taggamelocation":
                Practice.getInstance().getCustomLocationManager().setTnttagGameLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the TNTTag game location.");
                break;
            case "tagmax":
                Practice.getInstance().getCustomLocationManager().setTnttagMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the TNTTag max.");
                break;
            case "tagmin":
                Practice.getInstance().getCustomLocationManager().setTnttagMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the TNTTag min.");
                break;

            case "tntrunlocation": {
                Practice.getInstance().getCustomLocationManager().setTntLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the tntrun location.");
                break;
            }
            case "tntrunmin": {
                Practice.getInstance().getCustomLocationManager().setTntMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the tntrun location.");
                break;
            }
            case "tntrunmax": {
                Practice.getInstance().getCustomLocationManager().setTntMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the tntrun location.");
                break;
            }
            case "lmslocation":
                Practice.getInstance().getCustomLocationManager().setLmsLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the lms location.");
                break;
            case "lms":
                Practice.getInstance().getCustomLocationManager().getLmsLocations().add(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the LMS spawn-point #" + Practice.getInstance().getCustomLocationManager().getLmsLocations().size() + ".");
                break;
        }
    }
}
