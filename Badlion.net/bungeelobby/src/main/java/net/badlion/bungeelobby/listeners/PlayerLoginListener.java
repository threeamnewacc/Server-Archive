package net.badlion.bungeelobby.listeners;

import net.badlion.bungeelobby.BungeeLobby;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerLoginListener implements Listener {

    private boolean allowLogins = true;
    private ServerInfo lobby9;
    private ServerInfo lobby;

    public PlayerLoginListener() {
        this.allowLogins = BungeeLobby.config.getConfig().getBoolean("allow-logins", true);

        List<String> servers = BungeeCord.getInstance().getConfig().getListeners().iterator().next().getServerPriority();
        for (String server : servers) {
            ServerInfo serverInfo = BungeeCord.getInstance().getServerInfo(server);
            if (server.contains("9")) {
                lobby9 = serverInfo;
            } else {
                lobby = serverInfo;
            }
        }
    }

    @EventHandler
    public void onPlayerPreLogin(final PreLoginEvent event) {
        if (this.allowLogins) {
            return;
        }

        event.setCancelled(true);
        BungeeCord.getInstance().getLogger().info(ChatColor.BLUE + event.getConnection().getName() + " tried to connect to mc.badlion.net");
        event.setCancelReason("\n\n" + ChatColor.RED + "Please connect using " + ChatColor.YELLOW + "na.badlion.net" + ChatColor.RED + ",  " + ChatColor.YELLOW + "eu.badlion.net" + ChatColor.RED + " or " + ChatColor.YELLOW + "au.badlion.net" + ChatColor.RED + " instead." +
                                      "\n\n" +
                                      "Por favor conéctate usando " + ChatColor.YELLOW + "na.badlion.net" + ChatColor.RED + ", " + ChatColor.YELLOW + "eu.badlion.net " + ChatColor.RED + " or " + ChatColor.YELLOW + "au.badlion.net");
    }
	
	@EventHandler
	public void onPlayerPostLogin(final PostLoginEvent event) {
        if (this.allowLogins) {
            return;
        }

        BungeeCord.getInstance().getScheduler().schedule(BungeeLobby.plugin, new Runnable() {

            @Override
            public void run() {
                event.getPlayer().disconnect(ChatColor.RED + "Please connect using " + ChatColor.YELLOW + "na.badlion.net" + ChatColor.RED + ",  " + ChatColor.YELLOW + "eu.badlion.net" + ChatColor.RED + " or " + ChatColor.YELLOW + "au.badlion.net" + ChatColor.RED + " instead.");
            }
        }, 300, TimeUnit.MILLISECONDS);

	}

	@EventHandler
	public void onPlayerKicked(ServerKickEvent event) {
        // Not on a server yet or no cancel server
        if (event.getKickedFrom() == null) {
            // Be Safe
            event.setCancelled(true);

            event.setCancelServer(BungeeLobby.getInstance().getProxy().getServerInfo(BungeeLobby.getInstance().getProxy().getConfig().getListeners().iterator().next().getDefaultServer()));
            return;
        }

		// Make sure we only set the server to their current server if they're trying to connect
		// Don't want to do the same if they get banned or kicked
		/*if (!event.getCancelServer().getName().equals(event.getPlayer().getServer().getInfo().getName())
				&& event.getState() == ServerKickEvent.State.CONNECTING) {
			event.setCancelServer(event.getPlayer().getServer().getInfo());
		} else */

        if (!event.getKickedFrom().getName().contains("bllobby")) {
		    event.getPlayer().sendMessage(ChatColor.RED + "You were kicked to the lobby: " + event.getKickReason());
		    event.setCancelled(true);

            event.setCancelServer(BungeeLobby.getInstance().getProxy().getServerInfo(BungeeLobby.getInstance().getProxy().getConfig().getListeners().iterator().next().getDefaultServer()));
        }
	}

}
