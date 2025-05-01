package net.badlion.sgrankedmatchmaker.listeners;

import net.badlion.sgrankedmatchmaker.SGRankedMatchMaker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LobbyListener implements Listener {

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        SGRankedMatchMaker.getInstance().prepLobby(event.getPlayer());
    }

}
