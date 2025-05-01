package net.badlion.potpvp.commands;

import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ScanArenasCommand extends GCommandExecutor {

    public ScanArenasCommand() {
        super(0); // 0 arg minimum
    }

    @Override
    public void onGroupCommand(Command command, String label, String[] args) {
	    for (Arena arena : ArenaManager.getAllArenasOfType(ArenaManager.ArenaType.BUILD_UHC)) {
            Bukkit.getLogger().info("Starting arena " + arena.getArenaName());
            arena.scan();
            Bukkit.getLogger().info("Finished arena " + arena.getArenaName());
        }

        for (Arena arena : ArenaManager.getAllArenasOfType(ArenaManager.ArenaType.SKYWARS)) {
            Bukkit.getLogger().info("Starting arena " + arena.getArenaName());
            arena.scan();
            Bukkit.getLogger().info("Finished arena " + arena.getArenaName());
        }

        for (Arena arena : ArenaManager.getAllArenasOfType(ArenaManager.ArenaType.BUILD_UHC_FFA)) {
            Bukkit.getLogger().info("Starting arena " + arena.getArenaName());
            arena.scan();
            Bukkit.getLogger().info("Finished arena " + arena.getArenaName());
        }

        //Arena arena = ArenaManager.getAllArenasOfType(ArenaManager.ArenaType.UHC_MEETUP).get(0);
        //arena.scan();

        //Bukkit.getLogger().info("Finished arena " + arena.getArenaName());
    }

    @Override
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command usage: /scanarenas");
    }

}
