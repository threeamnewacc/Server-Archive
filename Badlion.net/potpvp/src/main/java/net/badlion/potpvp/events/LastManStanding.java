package net.badlion.potpvp.events;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.bukkitevents.MessageEvent;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.EnderPearlManager;
import net.badlion.potpvp.managers.EventManager;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.tasks.EventTieTask;
import net.badlion.potpvp.tasks.lms.LMSInvisibilityTask;
import net.badlion.potpvp.tasks.lms.LMSPlayerPositionTrackerTask;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.*;

public class LastManStanding extends RefreshKitEvent {

    private static int NUM_OF_POINTS_FOR_KILL = 1;
    private static int NUM_OF_POINTS_FOR_LAST_KILL = 3;

    private int mostPoints;
    private UUID mostPointsPlayer;
    private Map<UUID, Integer> playerToPointsMap = new HashMap<>();
    private BukkitTask invisibilityTask;
    private BukkitTask positionTrackerTask;
    private int totalNumOfPlayers;

    public LastManStanding(Player creator, ItemStack eventItem, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) {
	    super(creator, eventItem, kitRuleSet, EventType.LMS, ArenaManager.ArenaType.LMS);

	    this.armorContents = armorContents;
	    this.inventoryContents = inventoryContents;
        this.maxPlayers = 100;
    }

    @Override
    public void startGame() {
	    super.startGame();

        // Cache all participants
        this.totalNumOfPlayers = this.players.size();

        List<Integer> spawnNumbers = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            spawnNumbers.add(i);
        }

        Collections.shuffle(spawnNumbers);

        this.mostPointsPlayer = this.players.get(0).getUniqueId();

        for (Player player : this.players) {
            // No EP glitches
            EnderPearlManager.remove(player);

            player.sendMessage(ChatColor.GOLD + "Welcome to Last Man Standing.");
            player.sendMessage(ChatColor.GOLD + "Kills give 1 point.  Last kill gives 3 points.");
            player.sendMessage(ChatColor.GOLD + "Last man standing wins.");
            this.playerToPointsMap.put(player.getUniqueId(), 0);

            // Scoreboard to 0
            Objective objective = player.getScoreboard().registerNewObjective(ChatColor.AQUA + "Scoreboard", "Scoreboard");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score score = objective.getScore(ChatColor.GOLD + "Points");
            score.setScore(0);

            // Remove stuff we don't want them to have
            PlayerHelper.healAndPrepPlayerForBattle(player);

            // Add invisibility
            if (!this.kitRuleSet.getName().equals("chicken")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 60 * 5, 1), true); // 5 min
            }

            Location location = ArenaManager.getWarp(this.arena.getArenaName() + "-" + spawnNumbers.remove(0));
	        player.setFallDistance(0F);
            player.teleport(location);

	        // Load kit
	        this.refreshKit(player, false);

            PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));

            try {
                GroupStateMachine.matchMakingState.push(GroupStateMachine.lmsState, PotPvP.getInstance().getPlayerGroup(player), this);
            } catch (IllegalStateTransitionException e) {
                PotPvP.getInstance().somethingBroke(player, PotPvP.getInstance().getPlayerGroup(player));
            }

            Gberry.log("LMS", "Setup " + player.getName());
        }

        if (!this.kitRuleSet.getName().equals("chicken")) {
            this.invisibilityTask = new LMSInvisibilityTask(this).runTaskTimer(PotPvP.getInstance(), 20 * 60 * 4, 20 * 60 * 4);
        }

        this.positionTrackerTask = new LMSPlayerPositionTrackerTask(this).runTaskTimer(PotPvP.getInstance(), 20 * 60 * 10, 20 * 60);

        // Start time limit task
        this.eventTieTask = new EventTieTask(this);
        this.eventTieTask.runTaskLater(PotPvP.getInstance(), 20 * 60 * this.getEventType().getMatchLength());
    }

    @Override
    public void endGame(boolean premature) {
        super.endGame(premature);

        if (!premature) {
            List<EventManager.EventStats> statsList = new ArrayList<>();
            for (UUID uuid : this.playerToPointsMap.keySet()) {
                EventManager.EventStats stats = new EventManager.EventStats(EventType.LMS, uuid, 0, 0, 0, 0, 0);

                int points = this.playerToPointsMap.get(stats.getUuid());
                if (this.mostPointsPlayer != null) {
                    if (stats.getUuid().equals(this.mostPointsPlayer)) {
                        points -= 2;
                    }
                }

                // Add kills
                stats.addKills(points);

                // Add win
                if (this.mostPointsPlayer != null && this.mostPointsPlayer.equals(stats.getUuid())) {
                    stats.addWin();
                } else {
                    stats.addDeath();
                }

                stats.addGame();
                statsList.add(stats);
            }

            EventManager.updateStats(statsList);
        }
    }

    public void addPointsToPlayer(UUID uuid, int numOfPoints) {
        Gberry.log("LMS", "Giving kill to " + PotPvP.getInstance().getUsernameFromUUID(uuid));
        Integer points = this.playerToPointsMap.get(uuid);
        points += numOfPoints;
        this.playerToPointsMap.put(uuid, points);

        Player player = PotPvP.getInstance().getServer().getPlayer(uuid);
        if (player != null) {
            Group group = PotPvP.getInstance().getPlayerGroup(player);
            State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);

            // They might have left the LMS
            if (GroupStateMachine.lmsState == currentState) {
                player.getScoreboard().resetScores(ChatColor.GOLD + "Points");
                Score score = player.getScoreboard().getObjective(ChatColor.AQUA + "Scoreboard").getScore(ChatColor.GOLD + "Points");
                score.setScore(points);
                Gberry.log("LMS", "Scoreboard for kill updated for " + player.getName());
            }
        }

        if (points > this.mostPoints) {
            Gberry.log("LMS", "New most points for " + PotPvP.getInstance().getUsernameFromUUID(uuid));
            this.mostPoints = points;
            this.mostPointsPlayer = uuid;
        }
    }

    public void removePlayer(Player player, String reason) {
        this.removePlayer(player, reason, null);
    }

    public void removePlayer(Player player, String reason, UUID killerUUID) {
        String killerName = PotPvP.getInstance().getUsernameFromUUID(killerUUID);
        if (killerName == null) killerName = "themself";
        Gberry.log("LMS", "Removing player " + player + " with killer " + killerName);

        // Messages
        if (reason.equalsIgnoreCase("death")) {
            MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
                                                                ChatColor.GOLD + player.getName() + " has been eliminated from Last Man Standing by "
                                                                        + killerName + ". " + this.players.size() + "/"
                                                                        + this.totalNumOfPlayers + " players remaining.", null, this.players);
            PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);
        } else if (reason.equalsIgnoreCase("dc") || reason.equalsIgnoreCase("spawn")) {
            MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
                                                                ChatColor.GOLD + player.getName() + " wimped out from Last Man Standing.", null, this.players);
            PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);
        }

        this.players.remove(player);

        Group group = PotPvP.getInstance().getPlayerGroup(player);
        try {
            GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.getInstance().getCurrentState(group), group);
        } catch (IllegalStateTransitionException e) {
            player.sendMessage(ChatColor.RED + "Internal error, contact an admin.");
            return;
        }

        Gberry.log("LMS", "Removing player " + player + " from GroupGame");

        if (killerUUID != null) {
            this.addPointsToPlayer(killerUUID, this.players.size() == 1 && this.started ?
                    LastManStanding.NUM_OF_POINTS_FOR_LAST_KILL : LastManStanding.NUM_OF_POINTS_FOR_KILL);

            Player killer = PotPvP.getInstance().getServer().getPlayer(killerUUID);
            if (killer != null && this.players.size() > 1) {
                this.refreshKit(killer, true);
            }
        }

        // No scoreboard
        player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();

        Gberry.log("LMS", "Death message sent for " + player.getName());

        if (this.players.size() == 0) {
            Gberry.log("LMS", "Player size is now zero.");

            if (this.invisibilityTask != null) {
                this.invisibilityTask.cancel();
                this.positionTrackerTask.cancel();
                this.invisibilityTask = null;
                this.positionTrackerTask = null;
            }
        }

        // Clean up and allow for a new match
        if (this.players.size() == 1 && this.started) {
            Gberry.log("LMS", "One player detected " + this.players.get(0).getName());

            MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
                    ChatColor.GOLD + this.players.get(0).getName() + " has won the Last Man Standing match.  Congrats!", null, this.players);
            PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);

            // See who had the most points
            messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
                    ChatColor.GOLD + PotPvP.getInstance().getUsernameFromUUID(this.mostPointsPlayer)
                            + " had the most kill points with a total of " + this.mostPoints + ".", null, this.players);
            PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);
            this.removePlayer(this.players.get(0), "");

            this.endGame(false);
            Gberry.log("LMS", "Game set to end");
        }
    }

    @Override
    public void handleDeath(Player player) {
        Gberry.log("LMS", player.getName() + " has died.");
        this.removePlayer(player, "death", this.lastDamage.get(player.getUniqueId()));
    }

    @Override
    public Location handleRespawn(Player player) {
        return PotPvP.getInstance().getDefaultRespawnLocation();
    }

    @Override
    public boolean handleQuit(Player player, String reason) {
        Gberry.log("LMS", player.getName() + " has quit.");
        this.removePlayer(player, reason, this.lastDamage.get(player.getUniqueId()));

        // We return false because we already transition them, don't want /spawn to do it again
        return false;
    }

}
