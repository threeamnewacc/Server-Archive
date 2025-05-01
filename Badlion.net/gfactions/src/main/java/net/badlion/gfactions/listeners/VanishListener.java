package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.commands.VanishCommand;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;

public class VanishListener implements Listener {

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
        final boolean isWarden = event.getPlayer().hasPermission("GFactions.warden");
        if (isWarden) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "Enabled Vanish");
            event.getPlayer().spigot().setCollidesWithEntities(false);
            event.getPlayer().setGameMode(GameMode.CREATIVE);
        }

        GFactions.plugin.getServer().getScheduler().runTaskLater(GFactions.plugin, new Runnable() {
            @Override
            public void run() {
                for (Player pl : GFactions.plugin.getServer().getOnlinePlayers()) {
                    // The person who just joined was a warden, everyone hides from them
                    if (isWarden) {
                        pl.hidePlayer(event.getPlayer());
                    }

                    // The person who joined needs to hide the wardens already online
                    if (!pl.spigot().getCollidesWithEntities()) {
                        event.getPlayer().hidePlayer(pl);
                    }
                }
            }
        }, 1L);
	}

	@EventHandler
	public void onChestOpened(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (VanishListener.isVanishedPlayer(player)) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Block block = event.getClickedBlock();
				if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
					if (block.getState() instanceof Chest) {
						Chest chest = (Chest) block.getState();
						event.setCancelled(true);
						event.setUseInteractedBlock(Event.Result.DENY);
						player.sendMessage(ChatColor.AQUA + "Silently opening chest.");
						player.openInventory(chest.getInventory());
					}
				}
			}
		}
	}

    public static boolean isVanishedPlayer(HumanEntity player) {
        return !player.isOp() && !((Player) player).spigot().getCollidesWithEntities();
    }

}

