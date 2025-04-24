// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.events;

import java.util.function.Consumer;
import club.minemen.core.clickable.Clickable;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class EventCountdownTask extends BukkitRunnable
{
    private final PracticeEvent event;
    private final int countdownTime;
    private int timeUntilStart;
    private boolean ended;
    
    public EventCountdownTask(final PracticeEvent event, final int countdownTime) {
        this.event = event;
        this.countdownTime = countdownTime;
        this.timeUntilStart = countdownTime;
    }
    
    public void run() {
        if (this.isEnded()) {
            return;
        }
        if (this.timeUntilStart <= 0) {
            if (this.canStart()) {
                this.event.start();
            }
            else {
                this.onCancel();
            }
            this.ended = true;
            return;
        }
        if (this.shouldAnnounce(this.timeUntilStart)) {
            final Clickable message = new Clickable(CC.B_GOLD + this.event.getName() + " is starting in " + this.getTime(this.timeUntilStart) + "! Click to join!", CC.GREEN + "Click to join!", "/joinevent " + this.event.getName());
            this.event.getPlugin().getServer().getOnlinePlayers().forEach(message::sendToPlayer);
        }
        --this.timeUntilStart;
    }
    
    public abstract boolean shouldAnnounce(final int p0);
    
    public abstract boolean canStart();
    
    public abstract void onCancel();
    
    private String getTime(int time) {
        final StringBuilder timeStr = new StringBuilder();
        int minutes = 0;
        if (time % 60 == 0) {
            minutes = time / 60;
            time = 0;
        }
        else {
            while (time - 60 > 0) {
                ++minutes;
                time -= 60;
            }
        }
        if (minutes > 0) {
            timeStr.append(minutes).append("m");
        }
        if (time > 0) {
            timeStr.append((minutes > 0) ? " " : "").append(time).append("s");
        }
        return timeStr.toString();
    }
    
    public void setTimeUntilStart(final int timeUntilStart) {
        this.timeUntilStart = timeUntilStart;
    }
    
    public void setEnded(final boolean ended) {
        this.ended = ended;
    }
    
    public PracticeEvent getEvent() {
        return this.event;
    }
    
    public int getCountdownTime() {
        return this.countdownTime;
    }
    
    public int getTimeUntilStart() {
        return this.timeUntilStart;
    }
    
    public boolean isEnded() {
        return this.ended;
    }
}
