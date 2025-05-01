package net.badlion.gcheat.listeners;

import net.badlion.gcheat.GCheat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VapeListener implements Listener, PluginMessageListener {

    public static Set<UUID> vapers = new HashSet<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§8 §8 §1 §3 §3 §7 §8 ");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        String str;
        try {
            str = new String(data);
        } catch (Exception ex) {
            str = "";
        }
        GCheat.plugin.logMessage(player, player.getName() + " logged in with Hacked Client Type E (" + str + ")");
        vapers.add(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        vapers.remove(event.getPlayer().getUniqueId());
    }
}
