package us.zonix.practice.runnable;

import java.util.UUID;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.events.oitc.OITCEvent;
import us.zonix.practice.events.oitc.OITCPlayer;
import us.zonix.practice.events.parkour.ParkourEvent;
import us.zonix.practice.events.parkour.ParkourPlayer;
import us.zonix.practice.util.timer.impl.EnderpearlTimer;

public class ExpBarRunnable implements Runnable {
    private final Practice plugin = Practice.getInstance();

    @Override
    public void run() {
        EnderpearlTimer timer = Practice.getInstance().getTimerManager().getTimer(EnderpearlTimer.class);

        for (UUID uuid : timer.getCooldowns().keySet()) {
            Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null) {
                long time = timer.getRemaining(player);
                int seconds = (int)Math.round((double)time / 1000.0);
                player.setLevel(seconds);
                player.setExp((float)time / 15000.0F);
            }
        }

        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            PracticeEvent<?> event = this.plugin.getEventManager().getEventPlaying(player);
            if (event != null && event instanceof OITCEvent) {
                OITCEvent oitcEvent = (OITCEvent)event;
                OITCPlayer oitcPlayer = oitcEvent.getPlayer(player.getUniqueId());
                if (oitcPlayer != null && oitcPlayer.getState() != OITCPlayer.OITCState.WAITING && oitcEvent.getGameTask() != null) {
                    int seconds = oitcEvent.getGameTask().getTime();
                    if (seconds >= 0) {
                        player.setLevel(seconds);
                    }
                }
            } else if (event != null && event instanceof ParkourEvent) {
                ParkourEvent parkourEvent = (ParkourEvent)event;
                ParkourPlayer parkourPlayer = parkourEvent.getPlayer(player.getUniqueId());
                if (parkourPlayer != null && parkourPlayer.getState() != ParkourPlayer.ParkourState.WAITING && parkourEvent.getGameTask() != null) {
                    int seconds = parkourEvent.getGameTask().getTime();
                    if (seconds >= 0) {
                        player.setLevel(seconds);
                    }
                }
            }
        }
    }
}
