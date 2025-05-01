package net.badlion.potpvp.events;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.bukkitevents.MessageEvent;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.EnderPearlManager;
import net.badlion.potpvp.managers.EventManager;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.potpvp.tasks.EventTieTask;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UHCMeetup extends Event {

	private UUID winner;

    private int totalNumOfPlayers;
	private Map<UUID, Integer> playerKillCount = new HashMap<>();

    public UHCMeetup(Player creator, ItemStack eventItem) {
	    super(creator, eventItem, KitRuleSet.buildUHCRuleSet, EventType.UHC_MEETUP, ArenaManager.ArenaType.UHC_MEETUP);

        this.minPlayers = 8;
        this.maxPlayers = 50;
    }

    @Override
    public void startGame() {
	    super.startGame();

        // Cache all participants
        this.totalNumOfPlayers = this.players.size();
        List<Integer> spawnNumbers = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            spawnNumbers.add(i);
        }

        Collections.shuffle(spawnNumbers);

        for (Player player : this.players) {
            player.setGameMode(GameMode.SURVIVAL);

            // No EP glitches
            EnderPearlManager.remove(player);

            player.sendMessage(ChatColor.GOLD + "Welcome to UHC Meetup.");
            player.sendMessage(ChatColor.GOLD + "The objective is to kill all other players. Last player alive wins.");

	        // Make their custom scoreboard
	        Objective objective = ScoreboardUtil.getObjective(player.getScoreboard(), "meetup", DisplaySlot.SIDEBAR, ChatColor.AQUA + "Badlion UHC Meetup");

	        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setSuffix("0");
	        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "left", ChatColor.GOLD + "Players ", ChatColor.GOLD + "Left: " + ChatColor.WHITE).setSuffix(this.totalNumOfPlayers + "");

	        objective.getScore(ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setScore(3);
	        objective.getScore(ChatColor.GOLD + "Left: " + ChatColor.WHITE).setScore(1);

	        // Spacer
	        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp1", "", " ");

	        objective.getScore(" ").setScore(2);

            // Remove stuff we don't want them to have
            PlayerHelper.healAndPrepPlayerForBattle(player);

            // Load kit
            KitHelper.loadKit(player, this.kitRuleSet);

            this.kitRuleSet.sendMessages(player);

            Location location = ArenaManager.getWarp(this.arena.getArenaName() + "-" + spawnNumbers.remove(0));
	        player.setFallDistance(0F);
            player.teleport(location);

            PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));

            try {
	            System.out.println("$$$$$ Pushing " + this + " for player " + player.getName() + " and group " + PotPvP.getInstance().getPlayerGroup(player));
	            GroupStateMachine.matchMakingState.push(GroupStateMachine.uhcMeetupState, PotPvP.getInstance().getPlayerGroup(player), this);
	            System.out.println("@@@ " + GameState.getGroupGame(PotPvP.getInstance().getPlayerGroup(player)));
            } catch (IllegalStateTransitionException e) {
                PotPvP.getInstance().somethingBroke(player, PotPvP.getInstance().getPlayerGroup(player));
            }

            Gberry.log("LMS", "Setup " + player.getName());
        }

        // Start time limit task
        this.eventTieTask = new EventTieTask(this);
        this.eventTieTask.runTaskLater(PotPvP.getInstance(), 20 * 60 * this.getEventType().getMatchLength());
    }

	private void resetScoreboard(Player player) {
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "left", ChatColor.GOLD + "Players ", ChatColor.GOLD + "Left: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp1", "", ScoreboardUtil.SAFE_TEAM_PREFIX + " ").unregister();

		player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
	}

	@Override
	public void endGame(boolean premature) {
		super.endGame(premature);

		List<EventManager.EventStats> statsList = new ArrayList<>();
        if (this.participants != null) {
	        for (Player player : this.participants) {
		        EventManager.EventStats stats = new EventManager.EventStats(EventType.UHC_MEETUP, player.getUniqueId(), 0, 0, 0, 0, 0);

		        // Add kills
		        Integer kills = this.getPlayerKillCount(player);
		        stats.addKills(kills != null ? kills : 0);

		        // Add a death
		        if (!this.players.contains(player)) {
			        stats.addDeath();
		        }

		        if (this.winner != null && this.winner.equals(stats.getUuid())) {
			        stats.addWin();
		        }

		        stats.addGame();
		        statsList.add(stats);
	        }
        }

		EventManager.updateStats(statsList);
	}

    public void addKillForPlayer(UUID uuid) {
        Player player = PotPvP.getInstance().getServer().getPlayer(uuid);
        if (player != null) {
	        // Are they still in the UHC Meetup?
	        if (this.players.contains(player)) {
		        Integer kills = this.getPlayerKillCount(player);
		        if (kills != null) {
			        kills += 1;
			        this.playerKillCount.put(player.getUniqueId(), kills);
		        } else {
			        kills = 1;
			        this.playerKillCount.put(player.getUniqueId(), 1);
		        }

		        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setSuffix(kills + "");
		        Gberry.log("UHCMEETUP", "Scoreboard for kill updated for " + player.getName());
	        }
        }
    }

    public void removePlayer(Player player, String reason) {
        this.removePlayer(player, reason, null);
    }

    public void removePlayer(Player player, String reason, UUID killerUUID) {
        String killerName = PotPvP.getInstance().getUsernameFromUUID(killerUUID);
        Gberry.log("UHCMEETUP", "Removing player " + player + " with killer " + killerName);
        this.players.remove(player);

        Group group = PotPvP.getInstance().getPlayerGroup(player);
        try {
            GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.getInstance().getCurrentState(group), group);
        } catch (IllegalStateTransitionException e) {
            player.sendMessage(ChatColor.RED + "Internal error, contact an admin.");
            return;
        }

	    Gberry.log("UHCMEETUP", "Removing player " + player + " from GroupGame");

        if (killerUUID != null) {
            this.addKillForPlayer(killerUUID);
        }

        // Reset scoreboard
        this.resetScoreboard(player);

        if (reason.equalsIgnoreCase("death")) {
	        MessageEvent messageEvent;
	        if (killerName != null) {
		        messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
				        ChatColor.GOLD + player.getName() + " has been killed by " +
						        killerName + " in UHC Meetup. " + this.players.size()
						        + "/" + this.totalNumOfPlayers + " players remaining.", null, this.players);
	        } else {                                  messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
			        ChatColor.GOLD + player.getName() + " has killed themself in UHC Meetup. " + this.players.size()
					        + "/" + this.totalNumOfPlayers + " players remaining.", null, this.players);

	        }
            PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);
        } else if (reason.equalsIgnoreCase("dc") || reason.equalsIgnoreCase("spawn")) {
            MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
                    ChatColor.GOLD + player.getName() + " pussied out from UHC Meetup.", null, this.players);
            PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);
        }

        Gberry.log("UHCMEETUP", "Death message sent for " + player.getName());

        // Clean up and allow for a new match
        if (this.players.size() == 1 && this.started) {
	        Player winner = this.players.get(0);
	        Gberry.log("UHCMEETUP", "One player detected " + winner.getName());

	        this.winner = winner.getUniqueId();

	        MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
			        ChatColor.GOLD + winner.getName() + " has won the UHC Meetup event.  Congrats!", this.players);
	        PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);

	        this.removePlayer(winner, "");

	        this.endGame(false);
	        Gberry.log("UHCMEETUP", "Game set to end");
        }

    }

    @Override
    public void handleDeath(Player player) {
        Gberry.log("UHCMEETUP", player.getName() + " has died.");

        // Decrement players left on scoreboard
	    for (Player pl : this.players) {
		    ScoreboardUtil.getTeam(pl.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "left", ChatColor.GOLD + "Players ", ChatColor.GOLD + "Left: " + ChatColor.WHITE).setSuffix(this.players.size() - 1 + "");
	    }

	    // Drop a Golden Head since we disabled crafting benches
	    Item item = player.getWorld().dropItemNaturally(player.getLocation(), ItemStackUtil.createGoldenHead());
	    this.arena.addItemDrop(item);

        this.removePlayer(player, "death", this.lastDamage.get(player.getUniqueId()));
    }

    @Override
    public Location handleRespawn(Player player) {
        return PotPvP.getInstance().getDefaultRespawnLocation();
    }

    @Override
    public boolean handleQuit(Player player, String reason) {
        Gberry.log("UHCMEETUP", player.getName() + " has quit.");
        this.removePlayer(player, reason, this.lastDamage.get(player.getUniqueId()));

        // We return false because we already transition them, don't want /spawn to do it again
        return false;
    }

	public Integer getPlayerKillCount(Player player) {
		return this.playerKillCount.get(player.getUniqueId());
	}

}
