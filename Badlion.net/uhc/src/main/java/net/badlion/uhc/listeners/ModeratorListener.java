package net.badlion.uhc.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.commands.VanishCommand;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ModeratorListener implements Listener {

	public static Set<String> hasPlayedBefore = new HashSet<>();

    @EventHandler(priority=EventPriority.LOW)
    public void onModeratorLogin(PlayerLoginEvent event) {
        if (UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD).contains(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))) {
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
        }
    }

	@EventHandler
	public void playerInteractEntityEvent(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD).contains(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))
				|| BadlionUHC.getInstance().getHost() != null && BadlionUHC.getInstance().getHost().getUUID().equals(event.getPlayer().getUniqueId())) {
			if (event.getRightClicked() instanceof Player) {
				Player clicked = (Player) event.getRightClicked();

				BukkitUtil.openInventory(player, this.createPlayerInventory(clicked));

				// Log fake command usage
				Gberry.recordCommandUsage(player, "uhcinvsee " + clicked.getName());
			}
		}
	}

    @EventHandler
    public void modJoinEvent(final PlayerJoinEvent event) {
        if (UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD).contains(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))) {
            BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
                @Override
                public void run() {
                    VanishCommand.vanishPlayer(event.getPlayer());
                    BadlionUHC.getInstance().addMuteBanPerms(event.getPlayer());
                    event.getPlayer().setIgnoreXray(true);
                }
            }, 1);
        }
    }

    @EventHandler
    public void onPlayerBanOrMutePlayer(PlayerCommandPreprocessEvent event) {
        if (UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD).contains(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))
                || BadlionUHC.getInstance().getHost() != null && BadlionUHC.getInstance().getHost().getUUID().equals(event.getPlayer().getUniqueId())) {
            if (event.getMessage().toLowerCase().startsWith("/mute") || event.getMessage().toLowerCase().startsWith("/ban") || event.getMessage().toLowerCase().startsWith("/unmute")) {
                String[] args = event.getMessage().split(" ");
                if (event.getPlayer().hasPermission("badlion.uhctrial") && !event.getPlayer().hasPermission("badlion.uhcsrhost")
                        && !ModeratorListener.hasPlayedBefore.contains(args[1].toLowerCase())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "Can only punish players who are on the server.");
                }
            }
        }
    }

	private Inventory createPlayerInventory(Player player) {
		Inventory inventory = BadlionUHC.getInstance().getServer().createInventory(null, 54,
				ChatColor.BOLD + ChatColor.AQUA.toString() + player.getDisguisedName() + "'s Inventory");

		// Fill inventory
		ItemStack[] armorContents = player.getInventory().getArmorContents();
		ItemStack[] inventoryContents = player.getInventory().getContents();

		// Fill in armor contents
		for (int i = 0; i < armorContents.length; i++) {
			inventory.setItem(i, armorContents[3-i]);
		}

		// Fill in main inventory contents
		for (int i = 9; i < inventoryContents.length; i++) {
			inventory.setItem(i, inventoryContents[i]);
		}

		// Fill in hotbar contents
		for (int i = 0; i < 9; i++) {
			inventory.setItem(i + 36, inventoryContents[i]);
		}

		return inventory;
	}

}
