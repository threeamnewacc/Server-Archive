package net.badlion.potpvp.states.party;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gguard.ProtectedRegion;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.inventories.party.*;
import net.badlion.statemachine.GState;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PartyState extends GState<Group> implements Listener {

    public static Player wantsAPartner;

    public PartyState() {
        super("party", "they are in a party.", GroupStateMachine.getInstance());
    }

    @Override
    public void before(Group element) {
        super.before(element);

        for (Player player : element.players()) {
            if (player.isDead()) {
                continue;
            }

	        // Needed to for party fight since StasisHandler only works for group1 not the newly created party
	        //player.setGameMode(GameMode.SURVIVAL);

	        // Player is party leader?
	        if (element.getLeader() == player) {
		        PartyState.givePlayerItems(player, true);
	        } else {
                PartyState.givePlayerItems(player, false);
	        }

            ProtectedRegion region = PotPvP.getInstance().getgGuardPlugin().getProtectedRegion(player.getLocation(),
                    PotPvP.getInstance().getgGuardPlugin().getProtectedRegions());

            // Teleport them only if they're not in the spawn region
            if (region != null && !region.getRegionName().equals("spawn")) {
                PotPvP.getInstance().healAndTeleportToSpawn(player);
            }

            PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));
        }
    }

    public static void givePlayerItems(final Player player, boolean leader) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        if (leader) {
            player.getInventory().setItem(0, PartyHelper.getRanked2v2Item());
            player.getInventory().setItem(1, PartyHelper.getRanked3v3Item());
            player.getInventory().setItem(2, PartyHelper.getRanked5v5Item());
            player.getInventory().setItem(5, PartyHelper.getPartyEventItem());
        }

        player.getInventory().setItem(3, PartyHelper.getPartyListingItem());
        player.getInventory().setItem(8, PartyHelper.getLeavePartyItem());

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

    public static void getPartyFromRandomQueue(Player player) {
        if (PartyState.wantsAPartner == null) {
            PartyState.wantsAPartner = player;

	        // Give player the the item to leave the party random queue
	        player.getInventory().clear();
	        player.getInventory().setArmorContents(new ItemStack[4]);

	        player.getInventory().setItem(8, PartyHelper.getLeavePartyRandomQueueItem());

	        //player.getInventory().setHeldItemSlot(8);

	        player.updateInventory();
        } else {
            // Force create a party and get their new group
            try {
                GroupStateMachine.partyRequestState.transition(GroupStateMachine.lobbyState, PotPvP.getInstance().getPlayerGroup(PartyState.wantsAPartner));
            } catch (IllegalStateTransitionException e) {
                PotPvP.getInstance().somethingBroke(PartyState.wantsAPartner, PotPvP.getInstance().getPlayerGroup(PartyState.wantsAPartner));
            }

	        // Create party
	        Group group = PartyHelper.handleCreate(player, GroupStateMachine.partyState, false);

	        // Add other player to the party
	        PartyHelper.addToPartyGroup(PartyState.wantsAPartner, group.getParty(), false);

	        player.sendMessage(ChatColor.GREEN + "A random party partner has been found!");
	        PartyState.wantsAPartner.sendMessage(ChatColor.GREEN + "A random party partner has been found!");

	        PartyState.wantsAPartner = null;
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
	        if (PartyState.wantsAPartner == event.getPlayer()) {
		        PartyState.wantsAPartner = null;
	        }
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

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                switch (player.getInventory().getHeldItemSlot()) {
                    case 0:
                        if (PotPvP.getInstance().isTournamentMode()) {
                            event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, 2v2 ranked is disabled.");
                            return;
                        }

                        Ranked2v2Inventory.openRanked2v2Inventory(player);
                        break;
                    case 1: // Ranked 3v3
                        if (PotPvP.getInstance().isTournamentMode()) {
                            event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, 3v3 ranked is disabled.");
                            return;
                        }

                        Ranked3v3Inventory.openRanked3v3Inventory(player);
                        break;
                    case 2: // Ranked 5v5
                        if (PotPvP.getInstance().isTournamentMode()) {
                            event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, 5v5 ranked is disabled.");
                            return;
                        }

                        Ranked5v5Inventory.openRanked5v5Inventory(player);
                        break;
	                case 3: // Party listing
		                PartyListInventory.openPartyListInventory(player);
		                break;
	                case 5: // Party events
		                if (PotPvP.getInstance().isTournamentMode()) {
			                event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, party events are disabled.");
			                return;
		                }

		                PartyEventsInventory.openPartyEventsInventory(player);
		                break;
	                case 8: // Leave party
		                Gberry.log("PARTY", player.getName() + " is leaving party using item");
		                player.performCommand("party leave");
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

            if (event.getRespawnLocation().equals(PotPvP.getInstance().getDefaultRespawnLocation()) || state == GroupStateMachine.partyState) {
                event.setRespawnLocation(PotPvP.getInstance().getSpawnLocation());
                PotPvP.getInstance().healAndTeleportToSpawn(event.getPlayer());

                if (PotPvP.getInstance().getPlayerGroup(event.getPlayer()).getLeader() == event.getPlayer()) {
                    PartyState.givePlayerItems(event.getPlayer(), true);
                } else {
                    PartyState.givePlayerItems(event.getPlayer(), false);
                }
            }
        }
    }

}
