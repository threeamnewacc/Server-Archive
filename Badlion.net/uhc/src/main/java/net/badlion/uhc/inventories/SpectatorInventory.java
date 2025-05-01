package net.badlion.uhc.inventories;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SpectatorInventory extends BukkitRunnable {

	private static SmellyInventory lionInventory;
	private static SmellyInventory donatorPlusInventory;

	public static void initialize() {
		// Create smelly inventories
		SpectatorInventory.lionInventory = new SmellyInventory(new SpectatePlayerInventoryScreenHandler(), 54,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Alive Players");
		SpectatorInventory.donatorPlusInventory = new SmellyInventory(new SpectatePlayerInventoryScreenHandler(), 54,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Alive Players");

		new SpectatorInventory().runTaskTimer(BadlionUHC.getInstance(), 100L, 100L);
	}

	public static void openSpectateInventory(Player player) {
		if (player.hasPermission("badlion.lion")) {
			BukkitUtil.openInventory(player, SpectatorInventory.lionInventory.getMainInventory());
		} else {
			BukkitUtil.openInventory(player, SpectatorInventory.donatorPlusInventory.getMainInventory());
		}
	}

	private static ItemStack getSkullForPlayer(Player player) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setDisplayName(ChatColor.GREEN + player.getDisguisedName());
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "Click to teleport to");
		lore.add(ChatColor.YELLOW + player.getDisguisedName());
		skullMeta.setLore(lore);
		//skullMeta.setOwner(group.getLeader().getName());
		skull.setItemMeta(skullMeta);

		return skull;
	}

	@Override
	public void run() {
		// Clear the inventories
		SpectatorInventory.lionInventory.getMainInventory().clear();
		SpectatorInventory.donatorPlusInventory.getMainInventory().clear();

		for (UHCPlayer uhcPlayer : UHCPlayerManager.getAllUHCPlayers()) {
			if (uhcPlayer.getState() == UHCPlayer.State.PLAYER) {
				Player player = uhcPlayer.getPlayer();
				if (player != null && player.isOnline()) {
					if (player.getWorld() == BadlionUHC.getInstance().getUHCWorld()
							&& player.getLocation().getX() <= 500 && player.getLocation().getZ() <= 500
							&& player.getLocation().getX() >= -500 && player.getLocation().getZ() >= -500) {
						// Add to lion inventory
						SpectatorInventory.lionInventory.getMainInventory().addItem(SpectatorInventory.getSkullForPlayer(player));
						if (player.getLocation().getX() <= 100 && player.getLocation().getZ() <= 100
								&& player.getLocation().getX() >= -100 && player.getLocation().getZ() >= -100) {
							// Add to donator plus inventory
							SpectatorInventory.donatorPlusInventory.getMainInventory().addItem(SpectatorInventory.getSkullForPlayer(player));
						}
					}
				}
			}
		}
	}

	private static class SpectatePlayerInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			String name = item.getItemMeta().getDisplayName().substring(2);
			Player pl = SmellyInventory.plugin.getServer().getPlayerExact(name);
			if (pl != null && pl.isOnline() && UHCPlayerManager.getUHCPlayer(pl.getUniqueId()).getState() == UHCPlayer.State.PLAYER) {
				// Validate that the player isn't in the nether
				if (pl.getWorld().getEnvironment() == World.Environment.NETHER) {
					player.sendMessage(ChatColor.RED + "Cannot spectate that player.");
					return;
				}

				player.teleport(pl); // This automatically closes the opened inventory
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
