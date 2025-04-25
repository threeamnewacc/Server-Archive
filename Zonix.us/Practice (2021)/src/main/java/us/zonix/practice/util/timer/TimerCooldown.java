package us.zonix.practice.util.timer;

import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.zonix.practice.Practice;
import us.zonix.practice.util.timer.event.TimerExpireEvent;

public class TimerCooldown {
    private final Timer timer;
    private final UUID owner;
    private BukkitTask eventNotificationTask;
    private long expiryMillis;
    private long pauseMillis;

    protected TimerCooldown(Timer timer, long duration) {
        this.owner = null;
        this.timer = timer;
        this.setRemaining(duration);
    }

    protected TimerCooldown(Timer timer, UUID playerUUID, long duration) {
        this.timer = timer;
        this.owner = playerUUID;
        this.setRemaining(duration);
    }

    public long getRemaining() {
        return this.getRemaining(false);
    }

    protected void setRemaining(long milliseconds) throws IllegalStateException {
        if (milliseconds <= 0L) {
            this.cancel();
        } else {
            long expiryMillis = System.currentTimeMillis() + milliseconds;
            if (expiryMillis != this.expiryMillis) {
                this.expiryMillis = expiryMillis;
                if (this.eventNotificationTask != null) {
                    this.eventNotificationTask.cancel();
                }

                long ticks = milliseconds / 50L;
                this.eventNotificationTask = (new BukkitRunnable() {
                        public void run() {
                            if (TimerCooldown.this.timer instanceof PlayerTimer && TimerCooldown.this.owner != null) {
                                ((PlayerTimer)TimerCooldown.this.timer)
                                    .handleExpiry(Practice.getInstance().getServer().getPlayer(TimerCooldown.this.owner), TimerCooldown.this.owner);
                            }

                            Practice.getInstance()
                                .getServer()
                                .getPluginManager()
                                .callEvent(new TimerExpireEvent(TimerCooldown.this.owner, TimerCooldown.this.timer));
                        }
                    })
                    .runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(this.getClass()), ticks);
            }
        }
    }

    protected long getRemaining(boolean ignorePaused) {
        return !ignorePaused && this.pauseMillis != 0L ? this.pauseMillis : this.expiryMillis - System.currentTimeMillis();
    }

    protected boolean isPaused() {
        return this.pauseMillis != 0L;
    }

    public void setPaused(boolean paused) {
        if (paused != this.isPaused()) {
            if (paused) {
                this.pauseMillis = this.getRemaining(true);
                this.cancel();
            } else {
                this.setRemaining(this.pauseMillis);
                this.pauseMillis = 0L;
            }
        }
    }

    protected void cancel() throws IllegalStateException {
        if (this.eventNotificationTask != null) {
            this.eventNotificationTask.cancel();
            this.eventNotificationTask = null;
        }
    }

    public Timer getTimer() {
        return this.timer;
    }

    public long getExpiryMillis() {
        return this.expiryMillis;
    }

    public long getPauseMillis() {
        return this.pauseMillis;
    }

    protected void setPauseMillis(long pauseMillis) {
        this.pauseMillis = pauseMillis;
    }
}
