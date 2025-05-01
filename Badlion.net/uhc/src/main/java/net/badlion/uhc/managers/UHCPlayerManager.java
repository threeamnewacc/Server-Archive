package net.badlion.uhc.managers;

import net.badlion.gberry.utils.MessageUtil;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.commands.VanishCommand;
import net.badlion.uhc.listeners.ModeratorListener;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UHCPlayerManager {

    private static Map<UHCPlayer.State, ConcurrentLinkedQueue<UHCPlayer>> stateToPlayers = new HashMap<>();
    private static Map<UUID, UHCPlayer> playerMap = new HashMap<>();

    public static void initialize() {
        for (UHCPlayer.State state : UHCPlayer.State.values()) {
            UHCPlayerManager.stateToPlayers.put(state, new ConcurrentLinkedQueue<UHCPlayer>());
        }
    }

    public static UHCPlayer addNewUHCPlayer(UUID uuid, String username, UHCPlayer.State state) {
        UHCPlayer uhcPlayer = new UHCPlayer(uuid, username, state);

        // Emulate their death
        if (state == UHCPlayer.State.SPEC) {
            uhcPlayer.setDeathTime(0L);
        }

        UHCPlayerManager.stateToPlayers.get(state).add(uhcPlayer);
        UHCPlayerManager.playerMap.put(uuid, uhcPlayer);
        BadlionUHC.getInstance().putUUID(uuid, uhcPlayer.getUsername());
	    ModeratorListener.hasPlayedBefore.add(uhcPlayer.getUsername().toLowerCase());
	    uhcPlayer.setTeam(new UHCTeam(uuid));

        return uhcPlayer;
    }

    public static boolean updateUHCPlayerState(UUID uuid, UHCPlayer.State state) {
        UHCPlayer uhcPlayer = UHCPlayerManager.playerMap.get(uuid);
        UHCPlayer.State oldState = uhcPlayer.getState();

	    System.out.println("CHANGING " + uhcPlayer.getUsername() + " TO " + state + " FROM " + oldState);

        try {
            uhcPlayer.setState(state);
        } catch (RuntimeException e) {
            return false;
        }

        if (state == UHCPlayer.State.HOST) {
            Player p = BadlionUHC.getInstance().getServer().getPlayer(uuid);
            if (p != null) {
                VanishCommand.vanishPlayer(p);
            }
        }

        UHCPlayerManager.stateToPlayers.get(oldState).remove(uhcPlayer);
        UHCPlayerManager.stateToPlayers.get(state).add(uhcPlayer);

        return true;
    }

    public static UHCPlayer getUHCPlayer(UUID uuid) {
        return UHCPlayerManager.playerMap.get(uuid);
    }

    public static ConcurrentLinkedQueue<UHCPlayer> getUHCPlayersByState(UHCPlayer.State state) {
        return UHCPlayerManager.stateToPlayers.get(state);
    }

    public static Collection<UHCPlayer> getAllUHCPlayers() {
        return UHCPlayerManager.playerMap.values();
    }

    /**
     * Used to remove people completely from being able to join (they dont have a state)
     */
    public static boolean removeUHCPlayer(UUID uuid) {
        UHCPlayer uhcPlayer = UHCPlayerManager.playerMap.remove(uuid);
        BadlionUHC.getInstance().removeUUID(uhcPlayer.getUsername());

        // Only remove the team if we are the last person on it
        if (uhcPlayer.getTeam().getSize() == 1) {
            UHCTeamManager.removeUHCTeam(uhcPlayer.getTeam());
        } else {
            uhcPlayer.getTeam().removePlayer(uhcPlayer.getUUID()); // Clean up teams (the rare bug)
        }

        return UHCPlayerManager.stateToPlayers.get(uhcPlayer.getState()).remove(uhcPlayer);
    }

    public static void updateHealthScores(Player p) {
        int healthScale = (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.SCOREBOARDHEALTHSCALE.name()).getValue();
        String playerListName = DisplaySlot.PLAYER_LIST.name();
        String belowNameName = DisplaySlot.BELOW_NAME.name();

        for (UHCPlayer.State state : UHCPlayer.State.values()) {
            ConcurrentLinkedQueue<UHCPlayer> uhcPlayers = UHCPlayerManager.getUHCPlayersByState(state);
            for (UHCPlayer uhcPlayer : uhcPlayers) {
                Player p1 = uhcPlayer.getPlayer();
                if (p1 == null) {
                    continue;
                }

                ScoreboardUtil.getObjective(p1.getScoreboard(), playerListName, DisplaySlot.PLAYER_LIST, playerListName).getScore(p.getPlayerListName()).setScore((int) Math.ceil(p.getHealth() * healthScale));
                ScoreboardUtil.getObjective(p1.getScoreboard(), belowNameName, DisplaySlot.BELOW_NAME, MessageUtil.HEART_WITH_COLOR).getScore(p.getDisguisedName()).setScore((int) Math.ceil(p.getHealth() * healthScale));
            }
        }
    }

}
