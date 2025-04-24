package me.devkevin.practice.command.general;

import lombok.Getter;
import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 17/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class SetEditorSpawnCommand extends PracticeCommand {

    @Getter
    private static Practice plugin = Practice.getInstance();

    @Command(name = "setspawn.editor", permission = Rank.DEVELOPER, inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        plugin.getCustomLocationManager().setEditor(CustomLocation.fromBukkitLocation(player.getLocation()));
        player.sendMessage(CC.GREEN + "Spawn Editor has been set!");
    }
}
