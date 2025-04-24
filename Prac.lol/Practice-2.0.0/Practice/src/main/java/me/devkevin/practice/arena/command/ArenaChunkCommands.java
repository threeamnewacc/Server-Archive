package me.devkevin.practice.arena.command;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.chunk.manager.ChunkRestorationManager;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 22/10/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ArenaChunkCommands extends PracticeCommand {

    private final String NO_ARENA = CC.RED + "That arena doesn't exist!";

    private void getHelpCommand(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8- &fChunks - Command Help"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&7 • &b/chunk save <arenaName> <copyNumber>"));
        player.sendMessage(CC.translate("&7 • &b/chunk reset <arenaName> <copyNumber>"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Override @Command(name = "chunk", permission = Rank.MANAGER)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            getHelpCommand(player);
            return;
        }

        Arena arena = getPlugin().getArenaManager().getArena(args[1]);
        int copyNumber = Integer.parseInt(args[2]);

        switch (args[0].toLowerCase()) {
            case "save":
                if (arena != null) {
                    if (arena.getStandaloneArenas().get(copyNumber) == null) {
                        player.sendMessage(CC.RED + "That copy doesn't exist!");
                        return;
                    }

                    ChunkRestorationManager.getIChunkRestoration().copy(arena.getStandaloneArenas().get(copyNumber));
                    player.sendMessage(CC.GREEN + "Successfully saved standalone arena chunks for " + CC.YELLOW + arena.getName() + CC.GREEN + "#" + copyNumber + ".");
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "reset":
                if (arena != null) {
                    if (arena.getStandaloneArenas().get(copyNumber) == null) {
                        player.sendMessage(CC.RED + "That copy doesn't exist!");
                        return;
                    }

                    ChunkRestorationManager.getIChunkRestoration().reset(arena.getStandaloneArenas().get(copyNumber));
                    player.sendMessage(CC.GREEN + "Successfully reset standalone arena chunks for " + CC.YELLOW + arena.getName() + CC.GREEN + "#" + copyNumber + ".");
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            default:
                break;
        }
    }
}
