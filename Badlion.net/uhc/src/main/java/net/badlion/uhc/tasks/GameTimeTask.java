package net.badlion.uhc.tasks;

import net.badlion.common.libraries.StringCommon;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimeTask extends BukkitRunnable {

    private static boolean initialized = false;
    private static int healTime;
    private static int pvpTime;
    private static boolean hasBorderShrinking;
    public static int seconds = 0;
    public static int minutes = 0;
    public static int hours = 0;

    public static int secondsInGame = 0;

    public static int NUM_OF_SPEC_ONLINE = 0;

    @Override
    public void run() {
        if (++GameTimeTask.seconds == 60) {
            ++GameTimeTask.minutes;
	        GameTimeTask.seconds = 0;
        }

        if (GameTimeTask.minutes == 60) {
            ++GameTimeTask.hours;
	        GameTimeTask.minutes = 0;
        }

        if (!GameTimeTask.initialized || seconds == 0 || seconds % 5 == 0) {
            GameTimeTask.NUM_OF_SPEC_ONLINE = 0;

            // Count how many spectators are online
            for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.SPEC)) {
                Player pl = uhcPlayer.getPlayer();
                if (pl != null) {
                    ++GameTimeTask.NUM_OF_SPEC_ONLINE;
                }
            }

            for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD)) {
                Player pl = uhcPlayer.getPlayer();
                if (pl != null) {
                    ++GameTimeTask.NUM_OF_SPEC_ONLINE;
                }
            }

            for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.HOST)) {
                Player pl = uhcPlayer.getPlayer();
                if (pl != null) {
                    ++GameTimeTask.NUM_OF_SPEC_ONLINE;
                }
            }

            // Figure out logic for dragon bar
            boolean initializing = false;
            if (!GameTimeTask.initialized) {
                GameTimeTask.healTime = BadlionUHC.getInstance().getConfigurator().getIntegerOption(BadlionUHC.CONFIG_OPTIONS.HEALTIME.name()).getValue() * 60;
                GameTimeTask.pvpTime = BadlionUHC.getInstance().getConfigurator().getIntegerOption(BadlionUHC.CONFIG_OPTIONS.PVPTIMER.name()).getValue() * 60;
                GameTimeTask.hasBorderShrinking = BadlionUHC.getInstance().getBorderShrink();
                --GameTimeTask.seconds; // One time only
                initializing = true;
            }

            // Update Scoreboard
            for (Player player : BadlionUHC.getInstance().getServer().getOnlinePlayers()) {
                UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
                uhcPlayer.updateScoreboard();

                // Three things Heal Time, PVP Time, Border Shrink Time
                // TODO: Might need to fix logic if pvp time is before heal time?
                if (GameTimeTask.getNumOfSeconds() < GameTimeTask.healTime) {
                    player.setBossBar(ChatColor.AQUA + "Time Until Final Heal " + StringCommon.niceTime(GameTimeTask.healTime - GameTimeTask.getNumOfSeconds(), false), 1F - (((float) GameTimeTask.getNumOfSeconds()) / GameTimeTask.healTime));
                } else if (GameTimeTask.getNumOfSeconds() < GameTimeTask.pvpTime) {
                    player.setBossBar(ChatColor.AQUA + "Time Until PVP Is Enabled " + StringCommon.niceTime(GameTimeTask.pvpTime - GameTimeTask.getNumOfSeconds(), false), 1F - (((float) (GameTimeTask.getNumOfSeconds() - GameTimeTask.healTime)) / (GameTimeTask.pvpTime - GameTimeTask.healTime)));
                } /*else if (GameTimeTask.hasBorderShrinking) {
                    BarAPI.setMessage(p, ChatColor.AQUA + "Time Until PVP Is Enabled " + Gberry.niceTime(GameTimeTask.pvpTime - GameTimeTask.getNumOfSeconds()), 100F - (((float) GameTimeTask.getNumOfSeconds()) / GameTimeTask.healTime));
                }*/else {
                    player.removeBossBar();
                }

            }

            // One time only
            if (initializing) {
                ++GameTimeTask.seconds;
            }

            GameTimeTask.initialized = true;
        }
    }

    public static int getNumOfSeconds() {
        return GameTimeTask.seconds + (GameTimeTask.minutes * 60) + (GameTimeTask.hours * 60 * 60);
    }

}
