package me.devkevin.practice.panel.commands.impl;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 15/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ForceChunkSaveCommand extends PracticeCommand {

    @Override @Command(name = "forcechunksave", permission = Rank.ADMIN, usage = "&cUsage: /forcechunksave <arena> <copy_number>")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 2) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        Arena arena = getPlugin().getArenaManager().getArena(args[0]);
        int copyNumber = Integer.parseInt(args[1]);

        if (arena != null) {
            if (arena.getStandaloneArenas().get(copyNumber) == null) {
                player.sendMessage(CC.translate("&cThat copy doesn't exist."));
                return;
            }

            //getPlugin().getChunkRestaurationManager().copy(arena.getStandaloneArenas().get(copyNumber));
            player.sendMessage(CC.translate("&aSuccessfully saved standalone arena chunks for " + arena.getName() + " #" + copyNumber + "."));
        } else {
            player.sendMessage(CC.translate("That arena doesn't exist."));
        }
    }
}
