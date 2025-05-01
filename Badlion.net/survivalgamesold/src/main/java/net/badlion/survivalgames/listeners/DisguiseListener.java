package net.badlion.survivalgames.listeners;

import net.badlion.disguise.events.PlayerDisguiseEvent;
import net.badlion.disguise.events.PlayerUndisguiseEvent;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.survivalgames.SGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DisguiseListener implements Listener {

    @EventHandler
    public void onPlayerDisguise(PlayerDisguiseEvent event) {
        if (SurvivalGames.getInstance().getState().ordinal() >= SurvivalGames.SGState.START_COUNTDOWN.ordinal()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Can no longer use /disguise");
            event.setCancelled(true);
        } else {
            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId());
            sgPlayer.setListName(event.getPlayer().getDisguisedName());
        }
    }

    @EventHandler
    public void onPlayerUndisguise(PlayerUndisguiseEvent event) {
        if (SurvivalGames.getInstance().getState().ordinal() >= SurvivalGames.SGState.START_COUNTDOWN.ordinal()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Can no longer use /undisguise");
            event.setCancelled(true);
        } else {
            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId());
            sgPlayer.setListName(event.getPlayer().getName());
        }
    }

}
