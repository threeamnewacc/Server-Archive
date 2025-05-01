package net.badlion.potpvp.commands;

import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class AddWarpCommand extends GCommandExecutor {

    public AddWarpCommand() {
        super(1); // 1 arg minimum
    }

    @Override
    public void onGroupCommand(Command command, String label, final String[] args) {
        PotPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(PotPvP.getInstance(), new Runnable() {
            @Override
            public void run() {
                ArenaManager.addWarp(args[0], AddWarpCommand.this.player, AddWarpCommand.this.player.getLocation());
            }
        });
    }

    @Override
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command usage: /addwarp [name]");
    }


}
