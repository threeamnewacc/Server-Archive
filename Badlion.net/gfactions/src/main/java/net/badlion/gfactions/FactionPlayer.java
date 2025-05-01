package net.badlion.gfactions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.gfactions.listeners.EnderPearlCDListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionPlayer {

    private UUID uuid;
    private String uuidString;
    private boolean initialized = false;
    private int i = 0;
    private int lastNumOfFactionMembersOnline = 0;

    private boolean pvpTimer = false;

    private boolean hasStronghold = false;
    private Map<String, String> strongholdStuff = new HashMap<>();

    private boolean hasKOTH = false;
    private String kothName = "";

    public FactionPlayer(UUID uuid) {
        this.uuid = uuid;
        this.uuidString = this.uuid.toString();
    }

    public enum SCOREBOARD_ENTRIES {
        COMBAT_TAG, FACTION_ONLINE, PVPTIMER, ENDERPEARL, SPACE, STRONGHOLD, KOTH
    }

    public void updateScoreboard() {
        final Player pl = GFactions.plugin.getServer().getPlayer(this.uuid);
        if (pl == null) {
            return;
        }

        Scoreboard scoreboard = pl.getScoreboard();
        Objective objective = ScoreboardUtil.getObjective(scoreboard, ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Badlion HCF", DisplaySlot.SIDEBAR, ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Badlion HCF");

        // PVPTimer
        if (this.i++ % 60 == 0 && GFactions.plugin.getMapNameToPvPTimeRemaining().containsKey(pl.getUniqueId().toString())) {
            long currentTime = System.currentTimeMillis();
            long joinTime = GFactions.plugin.getMapNameToJoinTime().get(this.uuid.toString());
            long timeRemaining = GFactions.plugin.getMapNameToPvPTimeRemaining().get(this.uuid.toString());
            timeRemaining -= currentTime - joinTime;

            Team team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.PVPTIMER.name(), ChatColor.BLUE + "PVP Protectio", "n Timer: " + ChatColor.WHITE);
            team.setSuffix("" + timeRemaining / 60000 + "m");
            objective.getScore("n Timer: " + ChatColor.WHITE).setScore(15);

            this.pvpTimer = true;
        } else if (i % 60 == 1 && this.pvpTimer && !GFactions.plugin.getMapNameToPvPTimeRemaining().containsKey(pl.getUniqueId().toString())) {
            this.pvpTimer = false;
            scoreboard.resetScores("n Timer: " + ChatColor.WHITE);
        }

        // Combat Tag
        Team team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.COMBAT_TAG.name(), ChatColor.YELLOW + "CombatTag ", "Timer: " + ChatColor.WHITE, "" + GFactions.plugin.getRemainingCombatTagTime(pl) / 1000 + "s");
        team.setSuffix("" + GFactions.plugin.getRemainingCombatTagTime(pl) / 1000 + "s");

        if (!this.initialized) {
            objective.getScore("Timer: " + ChatColor.WHITE).setScore(14);
        }

        // EnderPearl
        Long lastPlayerPearl = EnderPearlCDListener.lastThrow.get(pl.getUniqueId());
        int pearlTimer = 0;

        if (lastPlayerPearl != null) {
            long time = System.currentTimeMillis();
            if (lastPlayerPearl + EnderPearlCDListener.COOLDOWN > time) {
                pearlTimer = (int) ((lastPlayerPearl + EnderPearlCDListener.COOLDOWN - time) / 1000);
            }
        }

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.ENDERPEARL.name(), ChatColor.RED + "EnderPearl", " Timer: " + ChatColor.WHITE);
        team.setSuffix(pearlTimer + "s");

        if (!this.initialized) {
            objective.getScore(" Timer: " + ChatColor.WHITE).setScore(13);
        }

        // Faction Members Online
        FPlayer fPlayer = FPlayers.i.get(this.uuidString);
        int numOnline = fPlayer.getFaction().getId().equals("0") ? 0 : fPlayer.getFaction().getOnlinePlayers().size();

        // Lessen the load
        if (this.lastNumOfFactionMembersOnline != numOnline) {
            team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.FACTION_ONLINE.name(), ChatColor.GREEN + "Faction Memb", "ers: " + ChatColor.WHITE);
            team.setSuffix("" + numOnline);

            if (!this.initialized) {
                objective.getScore("ers: " + ChatColor.WHITE).setScore(12);
            }

            this.lastNumOfFactionMembersOnline = numOnline;
        }

        // Spacer 1 time only
        if (!this.initialized) {
            ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.SPACE.name(), ChatColor.GREEN + "---------", "---------");

            objective.getScore("---------").setScore(11);
        }

        // Stronghold spacer
        if (!this.hasStronghold && this.strongholdStuff.size() > 0) {
            ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.STRONGHOLD.name(), ChatColor.GOLD + "Stronghold ", " Keeps:");

            objective.getScore(" Keeps:").setScore(10);
        } else if (this.hasStronghold && this.strongholdStuff.size() == 0) {
            scoreboard.resetScores(" Keeps:");
        }

        boolean clear = false;
        int i = 9;
        int j = 0;
        for (Map.Entry<String, String> entry : this.strongholdStuff.entrySet()) {
            if (GFactions.plugin.getStronghold() == null && this.hasStronghold) {
                scoreboard.resetScores(" " + ChatColor.RED + "");
                this.hasStronghold = false;
                clear = true;
            } else {
                team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.STRONGHOLD.name() + i, entry.getKey(), " " + ChatColor.RED);
                team.setSuffix(entry.getValue());
                objective.getScore(" " + ChatColor.RED + "").setScore(i--);
                this.hasStronghold = true;
            }
        }

        if (clear) {
            this.strongholdStuff.clear();
        }

        // KOTH
        // Combat Tag
        if (GFactions.plugin.getKoth() != null && GFactions.plugin.getKoth().getKothScoreTracker() != null) {
            team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.KOTH.name(), ChatColor.AQUA + "KOTH ", GFactions.plugin.getKoth().getKothName().replace("_", " ") + ChatColor.WHITE);
            team.setSuffix(" " + GFactions.plugin.getKoth().getKothScoreTracker().getFirstPlaceScore() + "s");
            this.kothName = GFactions.plugin.getKoth().getKothName().replace("_", " ") + ChatColor.WHITE;

            if (!this.hasKOTH) {
                objective.getScore(this.kothName).setScore(6);
            }

            this.hasKOTH = true;
        }

        if (this.hasKOTH && GFactions.plugin.getKoth() == null) {
            scoreboard.resetScores(this.kothName);
            this.hasKOTH = false;
        }

        // Hack for levels
        //if (p.getLevel() > 50) {
        //    p.setLevel(50);
        //}


        this.initialized = true;
        /*Team team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.GAME_TIME.name(), ChatColor.GREEN + "Game ", "Time: " + ChatColor.WHITE, GameTimeTask.niceTime());
        team.setSuffix(GameTimeTask.niceTime());
        objective.getScore("Time: " + ChatColor.WHITE).setScore(10);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.YOUR_KILLS.name(), ChatColor.GREEN + "Your ", "Kills: " + ChatColor.WHITE, "" + this.kills);
        team.setSuffix("" + this.kills);
        objective.getScore("Kills: " + ChatColor.WHITE).setScore(9);

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.PLAYERS_LEFT.name(), ChatColor.GREEN + "Players ", "Left: " + ChatColor.WHITE, "" + UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER).size());
        team.setSuffix("" + UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER).size());
        objective.getScore("Left: " + ChatColor.WHITE).setScore(8);

        if (BadlionUHC.getInstance().getTeamType() == UHCTeam.TeamType.CUSTOM_TEAMS) {
            team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.TEAMS_LEFT.name(), ChatColor.GREEN + "Teams ", "Left: " + ChatColor.RESET + ChatColor.WHITE, "" + UHCTeamManager.getAllAlivePlayingTeams().size());
            team.setSuffix("" + UHCTeamManager.getAllAlivePlayingTeams().size());
            objective.getScore("Left: " + ChatColor.RESET + ChatColor.WHITE).setScore(7);
        }

        team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.CURRENT_BORDER.name(), ChatColor.GOLD + "Current ", "Border: " + ChatColor.WHITE, "" + BadlionUHC.getInstance().getWorldBorder().GetWorldBorder("uhcworld").getRadiusX());
        team.setSuffix("" + BadlionUHC.getInstance().getWorldBorder().GetWorldBorder("uhcworld").getRadiusX());
        objective.getScore("Border: " + ChatColor.WHITE).setScore(2);*/
    }

    public Map<String, String> getStrongholdStuff() {
        return strongholdStuff;
    }

}
