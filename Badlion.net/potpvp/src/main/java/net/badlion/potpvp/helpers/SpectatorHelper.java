package net.badlion.potpvp.helpers;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.states.spectator.FollowState;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SpectatorHelper {

	private static ItemStack compassItem;
	private static ItemStack spectateFFAItem;
	private static ItemStack spectateEventsItem;
	private static ItemStack spectateTDMItem;
	private static ItemStack spectatorToggleOffItem;

	public static void initialize() {
		SpectatorHelper.compassItem = ItemStackUtil.createItem(Material.COMPASS, ChatColor.GREEN + "Teleportation Tool");
		SpectatorHelper.spectateFFAItem = ItemStackUtil.createItem(Material.GOLD_AXE, ChatColor.GREEN + "Spectate FFA");
		SpectatorHelper.spectateEventsItem = ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Spectate Event");
		SpectatorHelper.spectateTDMItem = ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "Spectate TDM");
		SpectatorHelper.spectatorToggleOffItem = ItemStackUtil.createItem(Material.REDSTONE_TORCH_ON, ChatColor.GREEN + "Spectator Toggle Off");
	}

	public static void setGameModeCreative(Player player) {
		player.setGameMode(GameMode.CREATIVE);
		player.spigot().setCollidesWithEntities(false); // hax
		player.getInventory().clear();
		player.setFlying(true);
	}

	public static void activateSpectateGameMode(Group group) {
		State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
		try {
			currentState.transition(GroupStateMachine.spectatorState, group);
		} catch (IllegalStateTransitionException e) {
			group.sendLeaderMessage(ChatColor.RED + "Cannot use spectator at the moment.");
			return;
		}

		for (Player pl : group.players()) {
			SpectatorHelper.setGameModeCreative(pl);

            pl.getInventory().setItem(0, SpectatorHelper.compassItem);
            pl.getInventory().setItem(2, SpectatorHelper.spectateFFAItem);
            pl.getInventory().setItem(3, SpectatorHelper.spectateEventsItem);
			pl.getInventory().setItem(4, SpectatorHelper.spectateTDMItem);
			pl.getInventory().setItem(8, SpectatorHelper.spectatorToggleOffItem);

			pl.updateInventory();

			for (Player p2 : PotPvP.getInstance().getServer().getOnlinePlayers()) {
				// Show player if they aren't a spectator
				Group g = PotPvP.getInstance().getPlayerGroup(p2);
				if (!GroupStateMachine.spectatorState.contains(g)) {
					p2.hidePlayer(pl);
				} else {
					// Hide players from each other if they are a spectator
					pl.hidePlayer(p2);
					p2.hidePlayer(pl);
				}
			}

			pl.sendMessage(ChatColor.GREEN + "Spectator mode enabled. Use /sp [player] to teleport to a player.");
		}
	}

	public static void deactivateSpectateGameMode(Group group) {
        State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
        try {
            // Auto remove from following state
            if (currentState == GroupStateMachine.followState) {
                UUID followingUUID = FollowState.followerToPlayers.remove(group.getLeader().getUniqueId());
	            Player following = PotPvP.getInstance().getServer().getPlayer(followingUUID);
	            FollowState.playerToFollowers.get(following.getUniqueId()).remove(group.getLeader().getUniqueId());
            }

            GroupStateMachine.transitionBackToDefaultState(currentState, group);
        } catch (IllegalStateTransitionException e) {
            group.sendLeaderMessage(ChatColor.RED + "Cannot disable spectator at the moment.");
        }
    }

    public static void showSpectatorToEveryone(Group group) {
		for (Player pl : group.players()) {
			// Needed because they might have been in spawn already floating around
			PotPvP.getInstance().healAndTeleportToSpawn(pl);

			// Show to all other players
			for (Player p2 : PotPvP.getInstance().getServer().getOnlinePlayers()) {
				Group g = PotPvP.getInstance().getPlayerGroup(pl);
				if (!GroupStateMachine.spectatorState.contains(g)) {
					p2.showPlayer(pl);
				} else {
					// They are a spectator, show them
					p2.showPlayer(pl);
				}
			}

			pl.sendMessage(ChatColor.GREEN + "Spectator mode disabled.");
		}
	}

}
