package net.badlion.gfactions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Rel;
import net.badlion.gfactions.GFactions;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WrenchListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerPlaceSpawnerEvent(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		ItemStack item = event.getItemInHand();
		if (item != null && item.getType() == Material.MOB_SPAWNER) {
			if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
				String entityType = item.getItemMeta().getLore().get(0);
				CreatureSpawner spawner = ((CreatureSpawner) block.getState());
				spawner.setSpawnedType(EntityType.valueOf(entityType));
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem().getType() == Material.DIAMOND_HOE) {
			// Is this a wrench?
			ItemStack item = event.getItem();
			Player player = event.getPlayer();
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()
					&& item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Wrench")) {
				// Can the player use a wrench here?
				Faction faction = Board.getFactionAt(player.getLocation());
				FPlayer fPlayer = FPlayers.i.get(player);
				ProtectedRegion region = GFactions.plugin.getgGuardPlugin().getProtectedRegion(player.getLocation(),
						GFactions.plugin.getgGuardPlugin().getProtectedRegions());
				if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
					player.sendMessage(ChatColor.RED + "Not allowed to use that here.");
					return;
				} else if (region != null && !region.isAllowBrokenBlocks()) {
					player.sendMessage(ChatColor.RED + "Not allowed to use that here.");
					return;
				} else if (faction.getId().equals("-1") || faction.getId().equals("-2")) {
					player.sendMessage(ChatColor.RED + "Not allowed to use that here.");
					return;
				} else if (!faction.getId().equals("0") &&
						fPlayer.getRelationTo(faction) != Rel.LEADER && fPlayer.getRelationTo(faction) != Rel.OFFICER
						&& fPlayer.getRelationTo(faction) != Rel.MEMBER && fPlayer.getRelationTo(faction) != Rel.RECRUIT) {
					player.sendMessage(ChatColor.RED + "Not allowed to use that here.");
					return;
				}

				List<String> lore = item.getItemMeta().getLore();
				if (event.getClickedBlock().getType() == Material.MOB_SPAWNER) {
					try {
						String uses = lore.get(1);
						int usesLeft = Integer.valueOf(uses.substring(uses.length() - 1, uses.length()));

						if (usesLeft != 1) {
							player.sendMessage(ChatColor.RED + "You have no more Mob Spawner uses on your wrench!");
						} else {
							player.getInventory().setItemInHand(null);
							player.updateInventory();
							player.sendMessage(ChatColor.RED + "You have used your wrench to retrieve a Mob Spawner!");

							// Drop the mob spawner
							CreatureSpawner spawner = ((CreatureSpawner) event.getClickedBlock().getState());
							ItemStack spawnerItem = new ItemStack(Material.MOB_SPAWNER);
							ItemMeta spawnerItemMeta = spawnerItem.getItemMeta();
							List<String> spawnerItemLore = new ArrayList<>();
							spawnerItemLore.add(spawner.getSpawnedType().toString());
							spawnerItemMeta.setLore(spawnerItemLore);
							spawnerItem.setItemMeta(spawnerItemMeta);

							player.getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), spawnerItem);
							event.getClickedBlock().setType(Material.AIR);
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				} else if (event.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME) {
					// Drop the end portal frame
					player.getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), new ItemStack(Material.ENDER_PORTAL_FRAME));
					event.getClickedBlock().setType(Material.AIR);

					// Break end portal if there is one
					Block frame = event.getClickedBlock();
					Block firstPortal = null;

					// Find first portal block
					for (int x = -1; x < 2; x++) {
						for (int z = -1; z < 2; z++) {
							firstPortal = frame.getRelative(x, 0, z);

							if (firstPortal.getType() == Material.ENDER_PORTAL) {
								break;
							}
						}

						if (firstPortal.getType() == Material.ENDER_PORTAL) {
							break;
						}
					}

					// Is this frame even next to an end portal?
					if (firstPortal.getType() == Material.ENDER_PORTAL) {
						// Set to air
						firstPortal.setType(Material.AIR);

						// Now find the other 8
						int portalsFound = 1;
						Block portal = firstPortal;
						while (portalsFound < 9) {
							Block block;
							for (int x = -2; x < 3; x++) {
								for (int z = -2; z < 3; z++) {
									block = portal.getRelative(x, 0, z);

									if (block.getType() == Material.ENDER_PORTAL) {
										portalsFound++;
										block.setType(Material.AIR);
										portal = block;
									}
								}
							}
						}
					}

					// Remove a use from the lore
					String frameUses = lore.get(2);

					try {
						int usesLeft = Integer.valueOf(frameUses.substring(frameUses.length() - 1, frameUses.length())) - 1;
						lore.set(2, frameUses.substring(0, frameUses.length() - 1) + usesLeft);

						// Wrench all used up?
						if (usesLeft == 0) {
							player.getInventory().setItemInHand(null);
							player.updateInventory();
							player.sendMessage(ChatColor.RED + "You have used the last charge on your wrench to retrieve an End Portal Frame!");
						} else {

							// Is the mob spawner part 0?
							String s = lore.get(1);
							if (s.endsWith("1")) {
								lore.set(1, s.substring(0, s.length() - 1) + "0");
							}

							ItemMeta itemMeta = item.getItemMeta();
							itemMeta.setLore(lore);
							item.setItemMeta(itemMeta);

							player.sendMessage(ChatColor.YELLOW + "You have used a charge on your wrench to retrieve an End Portal Frame!");
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
