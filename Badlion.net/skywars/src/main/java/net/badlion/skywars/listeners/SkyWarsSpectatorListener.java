package net.badlion.skywars.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.listeners.SpectatorListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SkyWarsSpectatorListener implements Listener {

    private Set<UUID> clickedSnowball = new HashSet<>();

    public SkyWarsSpectatorListener() {
        ItemStack lobby = new ItemStack(Material.EYE_OF_ENDER);
        ItemMeta lobbyMeta = lobby.getItemMeta();
        lobbyMeta.setDisplayName(ChatColor.GOLD + "Go back to SkyWars Lobby");
        lobby.setItemMeta(lobbyMeta);

        ItemStack playAgain = new ItemStack(Material.SNOW_BALL);
        ItemMeta playAgainMeta = playAgain.getItemMeta();
        playAgainMeta.setDisplayName(ChatColor.GOLD + "Play Again");
        playAgain.setItemMeta(playAgainMeta);


        SpectatorListener.spectatorInventory[7] = lobby;
        SpectatorListener.spectatorInventory[8] = playAgain;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.spigot().getCollidesWithEntities()) {
            if (event.getItem() != null) {
                if (event.getItem().getType() == Material.EYE_OF_ENDER) {
                    Gberry.sendToServer(event.getPlayer(), MPG.SERVER_ON_END);
                } else if (event.getItem().getType() == Material.SNOW_BALL) {
                    if (this.clickedSnowball.contains(event.getPlayer().getUniqueId())) {
                        event.getPlayer().sendMessage(ChatColor.RED + "Already queued up for another game.");
                        return;
                    }

                    event.getPlayer().performCommand("playagain");
                    this.clickedSnowball.add(event.getPlayer().getUniqueId());
                }
            }

            // Always cancel this if not op
            if (!player.isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.clickedSnowball.remove(event.getPlayer().getUniqueId());
    }


}
