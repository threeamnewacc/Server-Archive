package net.badlion.skywars;

import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.skywars.tasks.SkyWarsGameTimeTask;
import net.badlion.smellymapvotes.VoteManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SkyPlayer extends MPGPlayer {

    private boolean ignoreFallDamage = true;
    private boolean destructionScore = false;
    private boolean deathmatchScore = false;

    private int tier1ChestsOpened = 0;
    private int tier2ChestsOpened = 0;
    private Set<Location> locationsOpened = new HashSet<>();

    private int mobsSpawned = 0;
    private int levels = 0;
    private int snowEggsShot = 0;
    private int snowEggsHit = 0;
    private int blocksPlaced = 0;

    public SkyPlayer(UUID uuid, String username) {
        super(uuid, username);
    }

    @Override
    public void giveWhitelistSlots() {

    }

    public void handleDeathban() {

    }

    public void heal() {
        getPlayer().setHealth(20);
        getPlayer().setFoodLevel(20);
        getPlayer().setFireTicks(0);
        for (PotionEffect pe : getPlayer().getActivePotionEffects()) {
            getPlayer().removePotionEffect(pe.getType());
        }
    }

    public void handleNewPlayer() {
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setArmorContents(null);
        heal();
    }

    public void handlePlayerDeathInternal() {
        super.handlePlayerDeathInternal();
    }

    public void handleSpectatorToPlayer() {

    }

    public void handleAliveToDeadPlayer(Player killer) {

    }

    public void handleLastAlivePlayerDisconnect() {

    }

    private void sendMessagesCommon() {
        Player p = this.getPlayer();
        if (p != null) {
            this.getPlayer().sendMessage(Gberry.getLineSeparator(org.bukkit.ChatColor.GOLD));
            VoteManager.sendVoteMessage(this.getPlayer(), SkyWars.getInstance().getCurrentGame().getGWorld().getInternalName());
            this.getPlayer().sendMessage("");
	        BaseComponent[] components = new ComponentBuilder("Go back to the")
			        .color(ChatColor.BLUE)
			        .append("[SkyWars Lobby]")
			        .color(ChatColor.GOLD)
			        .append("Click to go back")
			        .color(ChatColor.GOLD)
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					        new ComponentBuilder("Click to go back")
							        .color(ChatColor.GOLD)
							        .create()))
			        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					        "/server " + MPG.SERVER_ON_END))
			        .append(" or ")
			        .color(ChatColor.BLUE)
			        .append("[Play Again]")
			        .color(ChatColor.BLUE)
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					        new ComponentBuilder("Click to play again")
							        .color(ChatColor.GOLD)
							        .create()))
			        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					        "/playagain"))
			        .create();
	        this.getPlayer().spigot().sendMessage(components);
            this.getPlayer().sendMessage(Gberry.getLineSeparator(org.bukkit.ChatColor.GOLD));
        }
    }

    public void sendDeathMessages() {
        this.sendMessagesCommon();
    }

    public void sendWinnerMessages() {
        this.sendMessagesCommon();
    }

    @Override
    public Location handlePlayerRespawnScreen(Player player) {
        return SkyWars.getInstance().getCurrentGame().getWorld().getSpectatorLocation();
    }

    public enum SCOREBOARD_ENTRIES {
        NEXT_EVENT, YOUR_KILLS, TOP_KILLS, GAME_TIME, DESTRUCTION_TIME, DEATH_MATCH_TIME, SERVER, CURRENT_BORDER, PLAYERS_LEFT, SPECTATORS, WEBSITE, MODE, MAP
    }

	@Override
    public void update() {
        final Player player = SkyWars.getInstance().getServer().getPlayer(this.uuid);
        if (player == null) {
            return;
        }

        // Initialize
        if (player.getScoreboard().equals(SkyWars.getInstance().getServer().getScoreboardManager().getMainScoreboard())) {
            ScoreboardUtil.getNewScoreboard(player);
        }

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = ScoreboardUtil.getObjective(scoreboard, ChatColor.GOLD + "" + ChatColor.BOLD + "SkyWars", DisplaySlot.SIDEBAR, ChatColor.GOLD + "" + ChatColor.BOLD + "SkyWars");

        Team team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.NEXT_EVENT.name(), ChatColor.AQUA + "", "Next Event:" + ChatColor.WHITE);
        team.setSuffix("");
        objective.getScore("Next Event:" + ChatColor.WHITE).setScore(12);

        if (SkyWarsGameTimeTask.getInstance().isPastDestructionTime()) {
            // Remove the old timer
            if (!this.deathmatchScore) {
                team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.DESTRUCTION_TIME.name(), ChatColor.GREEN + "", "Destruction: " + ChatColor.WHITE);
                team.unregister();
                scoreboard.resetScores("Destruction: " + ChatColor.WHITE);
                this.deathmatchScore = true;
            }

            // Don't go negative
            if (SkyWarsGameTimeTask.getInstance().getTotalSeconds() <= SkyWarsGameTimeTask.DEATHMATCH_TIME) {
                team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.DESTRUCTION_TIME.name(), ChatColor.GREEN + "", "Deathmatch: " + ChatColor.WHITE);
                team.setSuffix(SkyWarsGameTimeTask.getInstance().getDeathmatchTime());
                objective.getScore("Deathmatch: " + ChatColor.WHITE).setScore(11);
            }
        }  else if (SkyWarsGameTimeTask.getInstance().isPastChestRefillTime()) {
            // Remove the old timer
            if (!this.destructionScore) {
                team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.GAME_TIME.name(), ChatColor.GREEN + "", "Refill " + ChatColor.WHITE);
                team.unregister();
                scoreboard.resetScores("Refill " + ChatColor.WHITE);
                this.destructionScore = true;
            }

            team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.DESTRUCTION_TIME.name(), ChatColor.GREEN + "", "Destruction: " + ChatColor.WHITE);
            team.setSuffix(SkyWarsGameTimeTask.getInstance().getDestructionTime());
            objective.getScore("Destruction: " + ChatColor.WHITE).setScore(11);
        } else {
            team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.GAME_TIME.name(), ChatColor.GREEN + "", "Refill " + ChatColor.WHITE);
            team.setSuffix(SkyWarsGameTimeTask.getInstance().getTimeTillChestRefill());
            objective.getScore("Refill " + ChatColor.WHITE).setScore(11);
        }

        // TODO: Teams left

        team = ScoreboardUtil.getTeam(scoreboard, "", ChatColor.GREEN + " ", "" + ChatColor.WHITE);
        team.setSuffix("");
        objective.getScore("" + ChatColor.WHITE).setScore(10);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.YOUR_KILLS.name(), ChatColor.GREEN + "Your ", "Kills: " + ChatColor.WHITE);
        team.setSuffix(this.getKills() + "");
        objective.getScore("Kills: " + ChatColor.WHITE).setScore(9);

        team = ScoreboardUtil.getTeam(scoreboard, " ", ChatColor.GREEN + "  ", " " + ChatColor.WHITE);
        team.setSuffix(" ");
        objective.getScore(" " + ChatColor.WHITE).setScore(8);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.PLAYERS_LEFT.name(), ChatColor.GREEN + "Players ", "Left: " + ChatColor.WHITE);
        team.setSuffix(MPGPlayerManager.getMPGPlayersByState(PlayerState.PLAYER).size() + "");
        objective.getScore("Left: " + ChatColor.WHITE).setScore(7);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.SPECTATORS.name(), ChatColor.GREEN + "Spectators ", ": " + ChatColor.WHITE);
        team.setSuffix(MPGPlayerManager.getMPGPlayersByState(PlayerState.SPECTATOR).size() + "");
        objective.getScore(": " + ChatColor.WHITE).setScore(6);

        team = ScoreboardUtil.getTeam(scoreboard, "  ", ChatColor.GREEN + "  ", " " + ChatColor.RESET);
        team.setSuffix(" ");
        objective.getScore(" " + ChatColor.RESET).setScore(5);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.MAP.name(), ChatColor.GREEN + "Ma", "p: " + ChatColor.WHITE);
        team.setSuffix(StringCommon.cleanEnum(SkyWars.getInstance().getCurrentGame().getGWorld().getNiceWorldName()));
        objective.getScore("p: " + ChatColor.WHITE).setScore(4);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.MODE.name(), ChatColor.GREEN + "Mod", "e: " + ChatColor.WHITE);
        team.setSuffix(SkyWars.getInstance().getCurrentGame().getGamemode().getName());
        objective.getScore("e: " + ChatColor.WHITE).setScore(3);

        team = ScoreboardUtil.getTeam(scoreboard, "   ", ChatColor.GREEN + "  ", " " + ChatColor.RESET + ChatColor.WHITE);
        team.setSuffix(" ");
        objective.getScore(" " + ChatColor.RESET + ChatColor.WHITE).setScore(2);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.WEBSITE.name(), ChatColor.AQUA + "", "www.badlion.net");
        team.setSuffix("");
        objective.getScore("www.badlion.net").setScore(1);
    }

    public boolean isIgnoreFallDamage() {
        return ignoreFallDamage;
    }

    public void setIgnoreFallDamage(boolean ignoreFallDamage) {
        this.ignoreFallDamage = ignoreFallDamage;
    }

    public void addChestOpened(Block block) {
        if (this.locationsOpened.contains(block.getLocation())) {
            return;
        }

        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getInventory();

        boolean isEmpty = true;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                isEmpty = false;
                break;
            }
        }

        if (isEmpty) {
            return;
        }

        if (SkyWars.getInstance().getCurrentGame().getTier1Chests().contains(inventory)) {
            this.addTier1ChestOpened();
        } else if (SkyWars.getInstance().getCurrentGame().getTier2Chests().contains(inventory)) {
            this.addTier2ChestsOpened();
        }

        this.locationsOpened.add(block.getLocation());
    }

    private void addTier1ChestOpened() {
        this.tier1ChestsOpened += 1;
    }

    private void addTier2ChestsOpened() {
        this.tier2ChestsOpened += 1;
    }

    public void addSpawnedMob() {
        this.mobsSpawned += 1;
    }

    public void addLevels(int num) {
        this.levels += num;
    }

    public void addSnowEggShot() {
        this.snowEggsShot += 1;
    }

    public void addSnowEggHit() {
        this.snowEggsHit += 1;
    }

    public void addBlockPlace() {
        this.blocksPlaced += 1;
    }

    public int getTier2ChestsOpened() {
        return tier2ChestsOpened;
    }

    public int getTier1ChestsOpened() {
        return tier1ChestsOpened;
    }

    public int getMobsSpawned() {
        return mobsSpawned;
    }

    public int getLevels() {
        return levels;
    }

    public int getSnowEggsHit() {
        return snowEggsHit;
    }

    public int getSnowEggsShot() {
        return snowEggsShot;
    }

    public int getBlocksPlaced() {
        return blocksPlaced;
    }

    @Override
    public String toString() {
        return this.username + " - " + this.playerState.name() + " - " + this.team.getDeaths();
    }
}
