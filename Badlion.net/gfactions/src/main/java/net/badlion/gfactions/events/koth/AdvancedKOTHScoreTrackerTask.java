package net.badlion.gfactions.events.koth;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.smellyloot.managers.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashSet;

public class AdvancedKOTHScoreTrackerTask extends KOTHScoreTrackerTask {

    private GFactions plugin;
    private KOTH koth;

    private Player playerCapping;
    private String firstPlaceName;
    private int firstPlaceScore;
    private boolean newPlayerCapping = false;
    public static HashSet<String> playersWhoWereInSpawn = new HashSet<>();

    public static int TIME_TO_WIN = 600; // Seconds

    public AdvancedKOTHScoreTrackerTask(GFactions plugin) {
        super(); // Does nothing
        this.plugin = plugin;
        this.firstPlaceName = "N/A";
        this.firstPlaceScore = TIME_TO_WIN;
        this.koth = null;
    }

    @Override
    public void run() {
        try {
            if (this.koth == null) {
                this.koth = this.plugin.getKoth();
            }

            // Screw this shit
            if (koth == null) {
                this.cancel();
            }

            int xMin = (int) this.koth.getCapzoneLocation1().getX();
            int yMin = (int) this.koth.getCapzoneLocation1().getY();
            int zMin = (int) this.koth.getCapzoneLocation1().getZ();
            int xMax = (int) this.koth.getCapzoneLocation2().getX();
            int yMax = (int) this.koth.getCapzoneLocation2().getY();
            int zMax = (int) this.koth.getCapzoneLocation2().getZ();

            // Just check if the person who was capping before is still there
            if (this.firstPlaceScore <= 0) {
                FPlayer fPlayer = FPlayers.i.get(this.playerCapping);
                Gberry.broadcastMessage(ChatColor.GREEN + this.playerCapping.getDisplayName() + ChatColor.GREEN + " has won KOTH for Faction " + ChatColor.GOLD + fPlayer.getFaction().getTag());
                Bukkit.getLogger().info(this.playerCapping.getName() + " " + this.playerCapping.getUniqueId().toString() + " has won KOTH for Faction " + fPlayer.getFaction().getTag() + " " + fPlayer.getFaction().getId());

                for (String fp : playersWhoWereInSpawn) {
                    Bukkit.getLogger().info(fp);
                }

                // Drop Loot
	            LootManager.dropEventLootPlayer("koth", this.playerCapping);

                GFactions.plugin.setKoth(null);
                this.cancel();
            } else if (this.playerCapping != null && this.playerCapping.isOnline() && !this.playerCapping.isDead()) {
                if (playerCapping.getLocation().getWorld().getName().equals("world")
                            && playerCapping.getLocation().getBlockX() >= xMin && playerCapping.getLocation().getBlockX() <= xMax
                            && playerCapping.getLocation().getBlockY() >= yMin && playerCapping.getLocation().getBlockY() <= yMax
                            && playerCapping.getLocation().getBlockZ() >= zMin && playerCapping.getLocation().getBlockZ() <= zMax) {
                    if (firstPlaceScore % 30 == 0) {
                        FPlayer fPlayer = FPlayers.i.get(this.playerCapping);
                        Gberry.broadcastMessage(ChatColor.GREEN + this.playerCapping.getDisplayName() + ChatColor.GREEN + " has captured KOTH for " + (TIME_TO_WIN - this.firstPlaceScore) + " seconds for Faction " + ChatColor.GOLD + fPlayer.getFaction().getTag());
                    }
                    firstPlaceScore--;
                } else {
                    Gberry.broadcastMessage(ChatColor.GREEN + this.playerCapping.getDisplayName() + ChatColor.GREEN + " is no longer capturing KOTH");
                    this.playerCapping = null;
                    this.firstPlaceScore = TIME_TO_WIN;
                    this.newPlayerCapping = true;
                }
            } else {
                for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
                    // Check if player is in the cap zone
                    Location location = player.getLocation();

                    // Find first person in cap zone, and they are the new capping player
                    if (!player.isDead()
                                && location.getWorld().getName().equals("world")
                                && location.getBlockX() >= xMin && location.getBlockX() <= xMax
                                && location.getBlockY() >= yMin && location.getBlockY() <= yMax
                                && location.getBlockZ() >= zMin && location.getBlockZ() <= zMax) {
                        FPlayer fPlayer = FPlayers.i.get(player);
                        if (fPlayer.getFaction().getId().equals("0")) {
                            continue;
                        }

                        player.sendMessage(ChatColor.BLUE + "You are now gaining points for the KOTH.");
                        Gberry.broadcastMessage(ChatColor.GREEN + player.getDisplayName() + ChatColor.GREEN + " is capturing KOTH for Faction " + ChatColor.GOLD + fPlayer.getFaction().getTag());
                        this.playerCapping = player;
                        this.newPlayerCapping = true;
                        this.firstPlaceScore = TIME_TO_WIN;
                        break;
                    }
                }
            }

            // Now we update
            this.newPlayerCapping = false;
            if (this.playerCapping != null) {
                this.firstPlaceName = this.playerCapping.getName();
            } else {
                this.firstPlaceName = "N/A";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleScoreboards() {
        // Update scoreboards again
        for (Player p : this.plugin.getServer().getOnlinePlayers()) {
            Scoreboard board = p.getScoreboard();

            Objective objective = board.getObjective("koth");

            // Incase of NPE
            if (objective == null) {
                objective = board.registerNewObjective("koth", "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setDisplayName(ChatColor.DARK_AQUA + "KOTH Score:");
            }

            // Update capping player's score
            if (this.playerCapping != null) {
                Score score1 = objective.getScore(this.playerCapping.getName());
                score1.setScore(this.firstPlaceScore);
            } else {
                Score score1 = objective.getScore("N/A");
                score1.setScore(TIME_TO_WIN);
            }

            if (this.newPlayerCapping) {
                // If one of these changed remove from the list, they will get re-added
                board.resetScores(this.firstPlaceName);
            }
        }
    }

    public Player getPlayerCapping() {
        return playerCapping;
    }

    public void setPlayerCapping(Player playerCapping) {
        this.playerCapping = playerCapping;
    }

    public String getFirstPlaceName() {
        return firstPlaceName;
    }

    public int getFirstPlaceScore() {
        return firstPlaceScore;
    }

    public void setNewPlayerCapping(boolean newPlayerCapping) {
        this.newPlayerCapping = newPlayerCapping;
    }
}
