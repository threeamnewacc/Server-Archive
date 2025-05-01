package net.badlion.uhc.commands;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MLGCommand implements CommandExecutor {

	public static boolean inMLG = false;

	// Players who are MLG'ing
	public static Set<UUID> mlgPlayers = new HashSet<>();

	// Players who are allowed to MLG (the winners)
	public static Set<UUID> allowedMLGPlayers = new HashSet<>();

	@Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		final Player player = (Player) sender;

		if (!MLGCommand.allowedMLGPlayers.contains(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "You can not do an MLG water bucket at this time!");
			return true;
		}

		if (MLGCommand.inMLG) {
			player.sendMessage(ChatColor.RED + "MLG has already started!");
			return true;
		}

		MLGCommand.allowedMLGPlayers.remove(player.getUniqueId());
		MLGCommand.mlgPlayers.add(player.getUniqueId());

		Gberry.broadcastMessage(ChatColor.AQUA + player.getName() + " is going to MLG!");
        return true;
    }

	public static void doMLG() {
		MLGCommand.inMLG = true;

		// Is no one doing MLG?
		if (MLGCommand.mlgPlayers.isEmpty()) {
			Gberry.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.YELLOW + "None of the winners signed up for the MLG challenge. SCARED???");
			return;
		}

		new BukkitRunnable() {
			int ticks = 5;
			int delay = 0;

			int mlgCount = 0;

			@Override
			public void run() {
				if (this.delay != 0) {
					this.delay--;
					return;
				}

				if (this.ticks == 0) {
					// Have they MLG'd 3 times already?
					if (this.mlgCount == 3) {
						StringBuilder sb = new StringBuilder();
						for (UUID uuid : MLGCommand.mlgPlayers) {
							Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);

							// Is player still alive?
							if (UHCPlayerManager.getUHCPlayer(uuid).getState() == UHCPlayer.State.PLAYER) {
								sb.append(", ");
								sb.append(player.getDisplayName());
							}
						}

						// Announce players who could MLG 3 times
						Gberry.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.AQUA + "Congratulations to " + sb.toString().substring(2) + ChatColor.AQUA + " for being an MLG master!");

						this.cancel();
						return;
					}

					Iterator<UUID> it = MLGCommand.mlgPlayers.iterator();
					while (it.hasNext()) {
						UUID uuid = it.next();
						Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);

						// Did player survive other MLG's?
						if (UHCPlayerManager.getUHCPlayer(uuid).getState() == UHCPlayer.State.PLAYER) {
							// Get a good block location to MLG above
							Block block = null;
							while (block == null || block.isLiquid()) {
								block = player.getWorld().getHighestBlockAt(Gberry.generateRandomInt(-20, 20), Gberry.generateRandomInt(-20, 20));
							}

							// Give them a water bucket
							player.getInventory().setHeldItemSlot(0);
							player.getInventory().setItem(0, new ItemStack(Material.WATER_BUCKET));
							player.updateInventory();

							// Teleport to location
							player.teleport(block.getLocation().add(0, Gberry.generateRandomInt(32, 72), 0));
						} else {
							it.remove();
						}
					}

					this.mlgCount++;

					// Reset and count down again
					this.ticks = 5;
					this.delay = 5;
				} else {
					// Is no one doing MLG?
					if (MLGCommand.mlgPlayers.isEmpty()) {
						this.cancel();
						return;
					}

					boolean everyoneDied = true;

					// Check if there's anyone alive
					for (UUID uuid : MLGCommand.mlgPlayers) {
						UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);
						if (uhcPlayer.getState() == UHCPlayer.State.PLAYER) {
							everyoneDied = false;
							break;
						}
					}

					// Did everyone die?
					if (everyoneDied) {
						Gberry.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.YELLOW + "No one was able to complete the MLG challenge! NOOBS!");

						this.cancel();
						return;
					}

					String s = "first";

					if (this.mlgCount == 1) s = "second";
					else if (this.mlgCount == 2) s = "third";

					Gberry.broadcastMessage(ChatColor.GOLD + "Winners " + s + " MLG at 0, 0 in " + this.ticks + "!");
					Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "CLICK", "UI_BUTTON_CLICK"), 1F, 1F);

					this.ticks--;
				}
			}
		}.runTaskTimer(BadlionUHC.getInstance(), 20L, 20L);
	}

}
