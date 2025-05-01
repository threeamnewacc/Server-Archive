package net.badlion.arenalobby.states;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.helpers.LobbyItemHelper;
import net.badlion.arenalobby.helpers.PartyHelper;
import net.badlion.arenalobby.inventories.lobby.EventQueueInventory;
import net.badlion.arenalobby.inventories.lobby.FFAInventory;
import net.badlion.arenalobby.inventories.lobby.KitCreationKitSelectionInventory;
import net.badlion.arenalobby.inventories.lobby.Ranked1v1Inventory;
import net.badlion.arenalobby.inventories.lobby.SettingsInventory;
import net.badlion.arenalobby.inventories.lobby.TournamentsInventory;
import net.badlion.arenalobby.inventories.lobby.Unranked1v1Inventory;
import net.badlion.arenalobby.inventories.party.PartyEventsInventory;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gguard.ProtectedRegion;
import net.badlion.statemachine.GState;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyState extends GState<Group> implements Listener {

	public LobbyState() {
		super("lobby", "they are in the lobby.", GroupStateMachine.getInstance());
	}

	@Override
	public void before(Group element) {
		super.before(element);

		for (Player player : element.players()) {
			if (player.isDead()) {
				continue;
			}

			LobbyState.givePlayerItems(player);

			ProtectedRegion region = ArenaLobby.getInstance().getgGuardPlugin().getProtectedRegion(player.getLocation(),
					ArenaLobby.getInstance().getgGuardPlugin().getProtectedRegions());

			// Teleport them only if they're not in the spawn region
			if (region != null && !region.getRegionName().equals("spawn")) {
				ArenaLobby.getInstance().healAndTeleportToSpawn(player);
			}

			// ArenaLobby.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player)); # TODO Fix?
		}
	}

	public static void givePlayerItems(final Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.getInventory().setItem(0, LobbyItemHelper.getKitCreationItem());
		player.getInventory().setItem(1, LobbyItemHelper.getRankedItem());
		player.getInventory().setItem(2, LobbyItemHelper.getUnrankedItem());
		player.getInventory().setItem(3, LobbyItemHelper.getTournamentsItem());

		player.getInventory().setItem(4, LobbyItemHelper.getFFAItem());

		// TODO: HARDCODE FOR DEVELOPMENT
		//if (Gberry.serverRegion == Gberry.ServerRegion.DEV) {
			player.getInventory().setItem(5, LobbyItemHelper.getEventsItem());
		//}

		player.getInventory().setItem(6, PartyHelper.getPartyEventItem());
		player.getInventory().setItem(7, LobbyItemHelper.getSpectateItem());
		player.getInventory().setItem(8, LobbyItemHelper.getSettingsItem());

		//player.getInventory().setHeldItemSlot(0);

		player.updateInventory();

		// Update again in a tick to fix edge cases
		BukkitUtil.runTaskNextTick(new Runnable() {
			@Override
			public void run() {
				player.updateInventory();
			}
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void playerInteractEvent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		Group group = ArenaLobby.getInstance().getPlayerGroup(player);
		if (this.contains(group)) {
			ItemStack item = event.getItem();

			event.setCancelled(true);

			if (item == null || item.getType().equals(Material.AIR)) return;

			// Are they loaded?
			if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK
					&& !PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId()).isLoaded()) {
				player.sendFormattedMessage("{0}Your data has not loaded yet, please try again in a few seconds.", ChatColor.RED);
				return;
			}

			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				switch (player.getInventory().getHeldItemSlot()) {
					case 0: // Kit creation
						KitCreationKitSelectionInventory.openKitCreationSelectionInventory(player);
						break;
					case 1: // Ranked
						if (ArenaLobby.getInstance().isTournamentMode()) {
							event.getPlayer().sendFormattedMessage("{0}Tournament mode is enabled, 1v1 ranked is disabled.", ChatColor.RED);
							return;
						}

						Ranked1v1Inventory.openRanked1v1Inventory(player);
						break;
					case 2: // Unranked
						Unranked1v1Inventory.openUnranked1v1Inventory(player);
						break;
					case 3: // Tournaments
						TournamentsInventory.openTournamentsInventory(player);
						break;
					case 4: // FFA
						// TODO: NEEDED?
						/*if (ArenaLobby.getInstance().isTournamentMode()) {
							event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, FFA is disabled.");
							return;
						} */

						FFAInventory.openFFAInventory(player);
						break;
					case 5: // Events
						// TODO: NEEDED?
						/*if (ArenaLobby.getInstance().isTournamentMode()) {
							event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, events are disabled.");
							return;
						}*/

						EventQueueInventory.openEventQueueInventory(player);
						break;
					case 6: // Party Events
						PartyEventsInventory.openPartyEventsInventory(player);
						break;
                    case 7: // Spectate
	                    // TODO: NEEDED?
	                    /*if (ArenaLobby.getInstance().isTournamentMode()) {
		                    if (!player.hasPermission("badlion.disguise")) {
			                    event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to spectate in the tournament, sorry!");
			                    return;
		                    }
	                    }*/

                        player.sendFormattedMessage("{0}You can spectate players by using ''{1}'' or ''{2}''. Do ''{3}'' for more information!", ChatColor.YELLOW, "/spec [name]", "/follow [name]", "/help");
                        break;
					case 8: // Settings
						SettingsInventory.openSettingsInventory(player);
						break;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Group group = ArenaLobby.getInstance().getPlayerGroup(event.getPlayer());
		if (this.contains(group)) {
			// Safety backup
			State<Group> state = GroupStateMachine.getInstance().getCurrentState(group);

			if (event.getRespawnLocation().equals(ArenaLobby.getInstance().getDefaultRespawnLocation()) || state == GroupStateMachine.lobbyState) {
				event.setRespawnLocation(ArenaLobby.getInstance().getSpawnLocation());
				ArenaLobby.getInstance().healAndTeleportToSpawn(event.getPlayer());

				LobbyState.givePlayerItems(event.getPlayer());
			}
		}
	}

}
