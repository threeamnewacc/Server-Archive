package club.minemen.hcfactions.modmode.profile;

import club.minemen.core.util.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ProfileListeners implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("modmode.staff")) {
			new Profile(event.getPlayer().getUniqueId());
		}

		for (Player other : Bukkit.getOnlinePlayers()) {
			if (event.getPlayer().equals(other)) {
				continue;
			}

			Profile profile = Profile.getByUuid(other.getUniqueId());

			if (profile != null) {
				if (profile.isVanished()) {
					if (!(event.getPlayer().hasPermission("modmode.staff"))) {
						event.getPlayer().hidePlayer(other);
					}
					else {
						Scoreboard scoreboard = event.getPlayer().getScoreboard();

						if (scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
							scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
							event.getPlayer().setScoreboard(scoreboard);
						}

						Team team = scoreboard.getTeam("hidden");

						if (team == null) {
							team = scoreboard.registerNewTeam("hidden");
							team.setPrefix(ChatColor.GRAY + "");
							team.setCanSeeFriendlyInvisibles(true);
						}

						team.addEntry(other.getName());
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Profile.getProfiles().remove(event.getPlayer().getUniqueId());

		if (profile != null) {
			profile.setVanished(false);
			event.getPlayer().getInventory().setContents(profile.getContents());
			event.getPlayer().getInventory().setArmorContents(profile.getArmor());
			event.getPlayer().setGameMode(profile.getGamemode());
		}
	}

	@EventHandler
	public void onPlayerInteractChest(PlayerInteractEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile != null) {
			if (profile.isVanished()) {
				event.setCancelled(true);

				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Block block = event.getClickedBlock();

					if (block.getState() instanceof ContainerBlock) {
						ContainerBlock container = (ContainerBlock) block.getState();

						Inventory copy = Bukkit.createInventory(null, container.getInventory().getSize(), container.getInventory().getTitle());

						for (int i = 0; i < container.getInventory().getSize(); i++) {
							copy.setItem(i, container.getInventory().getItem(i));
						}

						event.getPlayer().openInventory(copy);
						event.getPlayer().sendMessage(ChatColor.RED + "Displaying duplicate of " + block.getType().name().replace("_", "").toLowerCase() + " inventory.");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile != null) {
			ItemStack itemStack = event.getPlayer().getItemInHand();

			if (itemStack != null && event.getRightClicked() instanceof Player) {
				if (itemStack.getType() == Material.BOOK) {
					Bukkit.dispatchCommand(event.getPlayer(), "invsee " + (event.getRightClicked()).getName());
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile != null) {
			ItemStack itemStack = event.getItem();

			if (itemStack != null && event.getAction().name().contains("RIGHT")) {
				event.setCancelled(true);

				if (itemStack.getType() == Material.INK_SACK && itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName() != null && (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY + ChatColor.BOLD.toString() + "Become Invisible") || itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + ChatColor.BOLD.toString() + "Become Visible"))) {
					profile.setVanished(!profile.isVanished());
					event.getPlayer().setItemInHand(new ItemBuilder(Material.INK_SACK).durability(itemStack.getDurability() == 8 ? 10 : 8).name(itemStack.getDurability() == 8 ? ChatColor.GRAY + ChatColor.BOLD.toString() + "Become Invisible" : ChatColor.GREEN + ChatColor.BOLD.toString() + "Become Visible").build());
					return;
				}

				if (itemStack.getType() == Material.SKULL_ITEM) {
					List<Player> staff = new ArrayList<>();

					for (Player online : Bukkit.getOnlinePlayers()) {
						if (online.hasPermission("modmode.staff")) {
							staff.add(online);
						}
					}

					int rows = (int) Math.ceil(staff.size() / 9.0);

					Inventory inventory = Bukkit.createInventory(null, (rows == 0 ? 9 : 9 * rows), "Online Staff");

					for (Player member : staff) {
						inventory.addItem(new ItemBuilder(Material.SKULL_ITEM).durability(3).name(member.getDisplayName()).build());
					}

					event.getPlayer().openInventory(inventory);
				}

				if (itemStack.getType() == Material.DIAMOND_PICKAXE) {
					List<Player> players = new ArrayList<>();

					for (Player online : Bukkit.getOnlinePlayers()) {
						Profile onlineProfile = Profile.getByUuid(online.getUniqueId());

						if (online.getY() <= 20) {
							if (onlineProfile != null) {
								if (onlineProfile.isVanished()) {
									continue;
								}
							}

							players.add(online);
						}
					}

					int rows = (int) Math.ceil(players.size() / 9.0);

					Inventory inventory = Bukkit.createInventory(null, (rows == 0 ? 9 : 9 * rows), "X-Ray Finder");

					for (Player player : players) {
						inventory.addItem(new ItemBuilder(Material.SKULL_ITEM).durability(3).name(player.getDisplayName()).build());
					}

					event.getPlayer().openInventory(inventory);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());

		if (profile != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Profile profile = Profile.getByUuid(event.getDamager().getUniqueId());

			if (profile != null && profile.isVanished()) {
				event.setCancelled(true);
			}

		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

		if (profile != null) {
			event.getDrops().clear();
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile != null) {
			event.getItemDrop().remove();

			if (event.getPlayer().getItemInHand().getAmount() == 0) {
				event.getPlayer().setItemInHand(event.getItemDrop().getItemStack());
			}
			else {
				event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() + 1);
			}

			event.getPlayer().updateInventory();
		}
	}

}
