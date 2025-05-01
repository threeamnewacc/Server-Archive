package net.badlion.bungeelobby.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetSocketAddress;

public class ManagerServerCommand extends Command {

    public ManagerServerCommand() {
        super("manageserver", "bungeecord.command.alert");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            ServerInfo serverInfo = BungeeCord.getInstance().constructServerInfo(args[1], new InetSocketAddress(args[2], Integer.parseInt(args[3])), "", false);
            ProxyServer.getInstance().getConfig().addServer(serverInfo);
            sender.sendMessage(ChatColor.GREEN + "Server " + args[1] + " has been added.");
        } else if (args[0].equalsIgnoreCase("remove")) {
            ProxyServer.getInstance().getConfig().removeServerNamed(args[1]);
            sender.sendMessage(ChatColor.GREEN + "Server " + args[1] + " has been removed.");
        }
    }
}
