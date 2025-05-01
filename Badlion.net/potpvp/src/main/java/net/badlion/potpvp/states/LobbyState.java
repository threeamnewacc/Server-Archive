package net.badlion.potpvp.states;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gguard.ProtectedRegion;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.events.Event;
import net.badlion.potpvp.helpers.LobbyItemHelper;
import net.badlion.potpvp.helpers.SpectatorHelper;
import net.badlion.potpvp.inventories.lobby.*;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.statemachine.GState;
import net.badlion.statemachine.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

            ProtectedRegion region = PotPvP.getInstance().getgGuardPlugin().getProtectedRegion(player.getLocation(),
                    PotPvP.getInstance().getgGuardPlugin().getProtectedRegions());

			// Teleport them only if they're not in the spawn region
            if (region != null && !region.getRegionName().equals("spawn")) {
                PotPvP.getInstance().healAndTeleportToSpawn(player);
            }

            PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));
		}
	}

    public static void givePlayerItems(final Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        player.getInventory().setItem(0, LobbyItemHelper.getKitCreationItem());
        player.getInventory().setItem(1, LobbyItemHelper.getRanked1v1Item());
        player.getInventory().setItem(2, LobbyItemHelper.getCreatePartyItem());
        player.getInventory().setItem(3, LobbyItemHelper.getUnranked1v1Item());
        player.getInventory().setItem(4, LobbyItemHelper.getFFAItem());
        player.getInventory().setItem(5, LobbyItemHelper.getEventsItem());

        if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_7) {
            player.getInventory().setItem(6, LobbyItemHelper.getTDMItem());
        }

        player.getInventory().setItem(7, LobbyItemHelper.getSpectatorToggleOnItem());
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
        Event.removeEventCreating(event.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        if (this.contains(group)) {
            ItemStack item = event.getItem();

	        event.setCancelled(true);

            if (item == null || item.getType().equals(Material.AIR)) return;

            // Are they loaded?
            if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK
                    && !PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId()).isLoaded()) {
                player.sendMessage(ChatColor.RED + "Your data has not loaded yet, please try again in a few seconds.");
                return;
            }

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                switch (player.getInventory().getHeldItemSlot()) {
                    case 0: // Kit creation
	                    KitCreationKitSelectionInventory.openKitCreationSelectionInventory(player);
                        break;
                    case 1: // Ranked 1v1
	                    if (PotPvP.getInstance().isTournamentMode()) {
		                    event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, 1v1 ranked is disabled.");
		                    return;
	                    }

	                    Ranked1v1Inventory.openRanked1v1Inventory(player);
                        break;
                    case 2: // Create party
                        Gberry.log("PARTY", player.getName() + " is creating a party using item");
	                    player.performCommand("party create");
	                    break;
                    case 3: // Unranked
	                    if (PotPvP.getInstance().isTournamentMode()) {
		                    event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, 1v1 unranked is disabled.");
		                    return;
	                    }

	                    Unranked1v1Inventory.openUnranked1v1Inventory(player);
                        break;
                    case 4: // FFA
	                    if (PotPvP.getInstance().isTournamentMode()) {
		                    event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, FFA is disabled.");
		                    return;
	                    }

	                    FFAInventory.openFFAInventory(player);
                        break;
                    case 5: // Events
	                    if (PotPvP.getInstance().isTournamentMode()) {
		                    event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, events are disabled.");
		                    return;
	                    }

	                    EventsInventory.openEventsInventory(player);
                        break;
	                case 6: // TDM
		                if (PotPvP.getInstance().isTournamentMode()) {
			                event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, TDM is disabled.");
			                return;
		                }

		                TDMInventory.openTDMInventory(player);
		                break;
                    case 7: // Spectate
	                    if (PotPvP.getInstance().isTournamentMode()) {
		                    if (!player.hasPermission("badlion.disguise")) {
			                    event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to spectate in the tournament, sorry!");
			                    return;
		                    }
	                    }

                        // Player can't be in spectator mode if they're in lobby state, so this means they want to turn it on
                        SpectatorHelper.activateSpectateGameMode(group);
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
        Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());
        if (this.contains(group)) {
            // Safety backup
            State<Group> state = GroupStateMachine.getInstance().getCurrentState(group);

            if (event.getRespawnLocation().equals(PotPvP.getInstance().getDefaultRespawnLocation()) || state == GroupStateMachine.lobbyState) {
                event.setRespawnLocation(PotPvP.getInstance().getSpawnLocation());
                PotPvP.getInstance().healAndTeleportToSpawn(event.getPlayer());

                LobbyState.givePlayerItems(event.getPlayer());
            }
        }
    }

}
