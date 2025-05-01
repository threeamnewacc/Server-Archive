package net.badlion.bungeelobby.commands;

import net.badlion.bungeelobby.BungeeLobby;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusCommand extends Command {

    private static final long startTime = System.currentTimeMillis();

    public StatusCommand() {
        super("bungeestatus", "bungeecord.command.alert", "bstatus");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.YELLOW + "Name: " + BungeeLobby.BUNGEE_NAME);
        sender.sendMessage(ChatColor.YELLOW + "IP: " + BungeeLobby.cloudflareIP);
        SimpleDateFormat format = new SimpleDateFormat();
        Date dateNow = new Date();
        Date dateStart = new Date(startTime);
        sender.sendMessage(ChatColor.YELLOW + "Current time: " + format.format(dateNow));
        long uptime = System.currentTimeMillis() - startTime;
        long minutes = (uptime / 60 / 1000) % 60;
        long hours = (uptime / 60 / 60 / 1000) % 24;
        long days = (uptime / 24 / 60 / 60 / 1000);
        sender.sendMessage(ChatColor.YELLOW + "Startup time: " + format.format(dateStart) + " (" + days + " days, " + hours + " hours, " + minutes + " minutes)");
        sender.sendMessage(ChatColor.YELLOW + "Players: " + ProxyServer.getInstance().getPlayers().size());
        sender.sendMessage(ChatColor.YELLOW + "Max memory: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "M");
        sender.sendMessage(ChatColor.YELLOW + "Total memory: " + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "M");
        sender.sendMessage(ChatColor.YELLOW + "Free memory: " + Runtime.getRuntime().freeMemory() / 1024 / 1024 + "M");
    }
}
