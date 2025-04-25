package us.zonix.practice.events;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.util.Clickable;

public class EventCountdownTask extends BukkitRunnable {
    private static final int DEFAULT_COUNTDOWN_TIME = 60;
    private final PracticeEvent event;
    private final int countdownTime;
    private int timeUntilStart;
    private boolean ended;

    public EventCountdownTask(PracticeEvent event, int countdownTime) {
        this.event = event;
        this.countdownTime = countdownTime;
        this.timeUntilStart = countdownTime;
    }

    public EventCountdownTask(PracticeEvent event) {
        this(event, 60);
    }

    public void run() {
        if (!this.isEnded()) {
            if (this.timeUntilStart <= 0) {
                if (this.canStart()) {
                    Practice.getInstance().getServer().getScheduler().runTask(Practice.getInstance(), this.event::start);
                } else {
                    Practice.getInstance().getServer().getScheduler().runTask(Practice.getInstance(), this::onCancel);
                }

                this.ended = true;
            } else {
                if (this.shouldAnnounce(this.timeUntilStart)) {
                    String toSend = "";
                    String toSendDonor = "";
                    toSend = ChatColor.RED.toString()
                        + ChatColor.BOLD
                        + "[Event] "
                        + ChatColor.WHITE
                        + ""
                        + this.event.getName()
                        + " is starting soon. "
                        + ChatColor.GRAY
                        + "[Join]";
                    toSendDonor = ChatColor.GRAY
                        + "["
                        + ChatColor.BOLD
                        + "*"
                        + ChatColor.GRAY
                        + "] "
                        + ChatColor.RED.toString()
                        + ChatColor.BOLD
                        + (this.event.getHost() == null ? "Someone" : this.event.getHost().getName())
                        + ChatColor.WHITE
                        + " is hosting a "
                        + ChatColor.WHITE.toString()
                        + ChatColor.BOLD
                        + this.event.getName()
                        + " Event. "
                        + ChatColor.GRAY
                        + "[Join]";
                    if (this.event.getHost() != null) {
                        Clickable message = new Clickable(
                            this.event.getHost().hasPermission("practice.donator") ? toSendDonor : toSend,
                            ChatColor.GRAY + "Click to join this event.",
                            "/join " + this.event.getName()
                        );
                        Bukkit.getServer().getOnlinePlayers().forEach(message::sendToPlayer);
                    }
                }

                this.timeUntilStart--;
            }
        }
    }

    public boolean shouldAnnounce(int timeUntilStart) {
        return Arrays.asList(45, 30, 15, 10, 5).contains(timeUntilStart);
    }

    public boolean canStart() {
        return this.event.getBukkitPlayers().size() >= 2;
    }

    public void onCancel() {
        this.getEvent().sendMessage("&cNot enough players joined the event. The event has been cancelled.");
        this.getEvent().end();
        this.getEvent().getPlugin().getEventManager().setCooldown(0L);
    }

    private String getTime(int time) {
        StringBuilder timeStr = new StringBuilder();
        int minutes = 0;
        if (time % 60 == 0) {
            minutes = time / 60;
            time = 0;
        } else {
            while (time - 60 > 0) {
                minutes++;
                time -= 60;
            }
        }

        if (minutes > 0) {
            timeStr.append(minutes).append("m");
        }

        if (time > 0) {
            timeStr.append(minutes > 0 ? " " : "").append(time).append("s");
        }

        return timeStr.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EventCountdownTask)) {
            return false;
        } else {
            EventCountdownTask other = (EventCountdownTask)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                Object this$event = this.getEvent();
                Object other$event = other.getEvent();
                if (this$event == null ? other$event == null : this$event.equals(other$event)) {
                    if (this.getCountdownTime() != other.getCountdownTime()) {
                        return false;
                    } else {
                        return this.getTimeUntilStart() != other.getTimeUntilStart() ? false : this.isEnded() == other.isEnded();
                    }
                } else {
                    return false;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventCountdownTask;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Object $event = this.getEvent();
        result = result * 59 + ($event == null ? 43 : $event.hashCode());
        result = result * 59 + this.getCountdownTime();
        result = result * 59 + this.getTimeUntilStart();
        return result * 59 + (this.isEnded() ? 79 : 97);
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

    public void setTimeUntilStart(int timeUntilStart) {
        this.timeUntilStart = timeUntilStart;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public String toString() {
        return "EventCountdownTask(event="
            + this.getEvent()
            + ", countdownTime="
            + this.getCountdownTime()
            + ", timeUntilStart="
            + this.getTimeUntilStart()
            + ", ended="
            + this.isEnded()
            + ")";
    }
}
