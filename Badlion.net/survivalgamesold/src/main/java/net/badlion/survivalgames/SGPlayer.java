package net.badlion.survivalgames;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.survivalgames.tasks.GameTimeTask;
import net.badlion.survivalgames.util.ScoreboardUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SGPlayer {

    public static enum State {
        ALIVE, SPECTATOR
    }

    private UUID uuid;
    private String username;
    private String listName;
    private State state = State.ALIVE;
    private int kills;
    private int nonDeathMatchKills;
    private Scoreboard scoreboard;
    private long startTime = 0;
    private int tier1ChestsOpened = 0;
    private int tier2ChestsOpened = 0;
    private Set<Location> locationsOpened = new HashSet<>();
    private boolean gotKillsBeforeDM = true;
    private int position = -1;
    private boolean did10HeartsOfDmgBeforeDM = false;

    private Set<String> whitelistedNames = new HashSet<>();
    private int maxWhitelistSlots;

    public SGPlayer(final UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.state = State.ALIVE;

        Bukkit.getScheduler().runTaskLater(SurvivalGames.getInstance(), new Runnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.hasPermission("badlion.lion")) {
                    SGPlayer.this.maxWhitelistSlots = 1;
                } else {
                    SGPlayer.this.maxWhitelistSlots = 0;
                }
            }
        }, 1);
    }



    public UUID getUuid() {
        return uuid;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;

        if (this.state == State.SPECTATOR) {
            SurvivalGames.getInstance().getServer().getScheduler().runTaskLater(SurvivalGames.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Player player = SurvivalGames.getInstance().getServer().getPlayer(SGPlayer.this.uuid);

                    if (player != null) {
                        player.getInventory().clear();
                        player.setGameMode(GameMode.CREATIVE);
                        player.spigot().setCollidesWithEntities(false);

                        for (Player p : SurvivalGames.getInstance().getServer().getOnlinePlayers()) {
                            p.hidePlayer(player);

                            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(p.getUniqueId());
                            if (sgPlayer.getState() == State.SPECTATOR) {
                                player.hidePlayer(p);
                            }
                        }

                        player.getInventory().setContents(SurvivalGames.getInstance().getSpectatorItems());
                        player.updateInventory();

                        if (!player.getLocation().getWorld().getName().equals(SurvivalGames.getInstance().getGame().getGWorld().getInternalName())) {
                            for (Player p : SurvivalGames.getInstance().getServer().getOnlinePlayers()) {
                                SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(p.getUniqueId());
                                if (sgPlayer.getState() == State.ALIVE) {
                                    player.teleport(p);
                                    break;
                                }
                            }
                        }
                    }
                }
            }, 1);
        }
    }

    public void addKill() {
        this.kills += 1;

        // Not in deathmatch
        if (SurvivalGames.getInstance().getState() != SurvivalGames.SGState.DEATH_MATCH) {
            this.nonDeathMatchKills += 1;
        }
    }

    public void setNoKillsBeforeDM() {
        this.gotKillsBeforeDM = false;
    }

    // TODO: Fix for double chests, yolo
    public void addChestOpened(Block block) {
        Gberry.log("RATING", block.getLocation().toString());
        if (this.locationsOpened.contains(block.getLocation())) {
            return;
        }

        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getInventory();

        boolean isEmpty = true;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                Gberry.log("RATING", "Not empty");
                isEmpty = false;
                break;
            }
        }

        if (isEmpty) {
            return;
        }

        if (SurvivalGames.getInstance().getGame().getTier1Chests().contains(inventory)) {
            Gberry.log("RATING", "Added tier1");
            this.addTier1ChestOpened();
        } else if (SurvivalGames.getInstance().getGame().getTier2Chests().contains(inventory)) {
            Gberry.log("RATING", "Added tier2");
            this.addTier2ChestsOpened();
        }

        this.locationsOpened.add(block.getLocation());
    }

    public int calculateRatingDiff() {
        int change = 0;

        Gberry.log("RATING", this.getUsername());
        Gberry.log("RATING", "tier1 " + this.getTier1ChestsOpened());
        Gberry.log("RATING", "tier2 " + this.getTier2ChestsOpened());

        change -= this.getTier1ChestsOpened() * 5;
        change -= this.getTier2ChestsOpened() * 10;

        if (!this.gotKillsBeforeDM) {
            change += 500;
        } else {
            change -= this.nonDeathMatchKills * 100;

            Gberry.log("RATING", "kills " + this.getKills());
        }

        // Punish people who didn't contribute damage prior to deathmatch
        if (!this.did10HeartsOfDmgBeforeDM && SurvivalGames.getInstance().getState().ordinal() >= SurvivalGames.SGState.DEATH_MATCH.ordinal()) {
            change += 250;
        }

        return change;
    }

    private void addTier1ChestOpened() {
        this.tier1ChestsOpened += 1;
        Gberry.log("RATING", this.tier1ChestsOpened + "");
    }

    private void addTier2ChestsOpened() {
        this.tier2ChestsOpened += 1;
    }

    public enum SCOREBOARD_ENTRIES {
        YOUR_KILLS, TOP_KILLS, GAME_TIME, DEATH_MATCH_TIME, SERVER, CURRENT_BORDER, PLAYERS_LEFT, SPECTATORS
    }

    public void updateScoreboard() {
        final Player player = SurvivalGames.getInstance().getServer().getPlayer(this.uuid);
        if (player == null) {
            return;
        }

        // Initialize
        if (player.getScoreboard().equals(SurvivalGames.getInstance().getServer().getScoreboardManager().getMainScoreboard())) {
            this.scoreboard = ScoreboardUtil.getScoreboard(player);
        }

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = ScoreboardUtil.getObjective(scoreboard, ChatColor.AQUA + "Badlion SG", DisplaySlot.SIDEBAR, ChatColor.AQUA + "Badlion SG");

        Team team = null;
        if (SurvivalGames.getInstance().getGame().isDeathMatch()) {
            team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.DEATH_MATCH_TIME.name(), ChatColor.GREEN + "", "Time Left: " + ChatColor.WHITE, GameTimeTask.niceTime());
            team.setSuffix(GameTimeTask.niceTime());
            objective.getScore("Time Left: " + ChatColor.WHITE).setScore(10);
        } else {
            team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.GAME_TIME.name(), ChatColor.GREEN + "", "Time Left: " + ChatColor.WHITE, GameTimeTask.niceTime());
            team.setSuffix(GameTimeTask.niceTime());
            objective.getScore("Time Left: " + ChatColor.WHITE).setScore(10);
        }

        team = ScoreboardUtil.getTeam(scoreboard, "", ChatColor.GREEN + " ", "" + ChatColor.WHITE, "");
        team.setSuffix("");
        objective.getScore("" + ChatColor.WHITE).setScore(9);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.YOUR_KILLS.name(), ChatColor.GREEN + "Your ", "Kills: " + ChatColor.WHITE, this.kills + "");
        team.setSuffix(this.kills + "");
        objective.getScore("Kills: " + ChatColor.WHITE).setScore(8);

        team = ScoreboardUtil.getTeam(scoreboard, " ", ChatColor.GREEN + "  ", " " + ChatColor.WHITE, " ");
        team.setSuffix(" ");
        objective.getScore(" " + ChatColor.WHITE).setScore(7);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.PLAYERS_LEFT.name(), ChatColor.GREEN + "Players ", "Left: " + ChatColor.WHITE, GameTimeTask.ALIVE_PLAYERS + "");
        team.setSuffix(GameTimeTask.ALIVE_PLAYERS + "");
        objective.getScore("Left: " + ChatColor.WHITE).setScore(6);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.SPECTATORS.name(), ChatColor.GREEN + "Spectators ", ": " + ChatColor.WHITE, GameTimeTask.SPECTATOR_PLAYERS + "");
        team.setSuffix(GameTimeTask.SPECTATOR_PLAYERS + "");
        objective.getScore(": " + ChatColor.WHITE).setScore(5);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SGPlayer && this.getUuid().equals(((SGPlayer) o).getUuid());
    }

    @Override
    public int hashCode() {
        return this.getUuid().hashCode();
    }

    public String getUsername() {
        return username;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getKills() {
        return kills;
    }

    public int getTier1ChestsOpened() {
        return tier1ChestsOpened;
    }

    public int getTier2ChestsOpened() {
        return tier2ChestsOpened;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getListName() {
        if (this.listName == null) {
            return this.username;
        }
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public Set<String> getWhitelistedNames() {
        return whitelistedNames;
    }

    public int getNumOfWhitelists() {
        return this.whitelistedNames.size();
    }

    public void addWhitelist(String whitelistedName){
        SurvivalGames.addWhiteListedPlayer(whitelistedName);
        this.whitelistedNames.add(whitelistedName);
    }

    public int getMaxWhitelistSlots() {
        return maxWhitelistSlots;
    }

    public void setDid10HeartsOfDmgBeforeDM(boolean did10HeartsOfDmgBeforeDM) {
        this.did10HeartsOfDmgBeforeDM = did10HeartsOfDmgBeforeDM;
    }
}
