package net.badlion.bungeelobby.commands;

import net.badlion.bungeelobby.BungeeLobby;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BIPCommand extends Command {

    public BIPCommand() {
        super("ip");
    }

    @Override
    public void execute(CommandSender sender, final String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

	    player.sendMessage(ChatColor.GREEN + "Current Bungee IP: " + ChatColor.GOLD + BungeeLobby.config.getConfig().getString("cloudflare.ip"));
    }

}
