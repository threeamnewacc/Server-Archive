package us.zonix.practice.util;

import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class StatusCache extends BukkitRunnable {
    private static StatusCache instance;
    private int fighting;
    private int queueing;

    public StatusCache() {
        instance = this;
    }

    public void run() {
        int fighting = 0;
        int queueing = 0;

        for (PlayerData playerData : Practice.getInstance().getPlayerManager().getAllData()) {
            if (playerData.getPlayerState() == PlayerState.FIGHTING) {
                fighting++;
            }

            if (playerData.getPlayerState() == PlayerState.QUEUE) {
                queueing++;
            }
        }

        this.fighting = fighting;
        this.queueing = queueing;
    }

    public int getFighting() {
        return this.fighting;
    }

    public int getQueueing() {
        return this.queueing;
    }

    public void setFighting(int fighting) {
        this.fighting = fighting;
    }

    public void setQueueing(int queueing) {
        this.queueing = queueing;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof StatusCache)) {
            return false;
        } else {
            StatusCache other = (StatusCache)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                return this.getFighting() != other.getFighting() ? false : this.getQueueing() == other.getQueueing();
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof StatusCache;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getFighting();
        return result * 59 + this.getQueueing();
    }

    public String toString() {
        return "StatusCache(fighting=" + this.getFighting() + ", queueing=" + this.getQueueing() + ")";
    }

    public static StatusCache getInstance() {
        return instance;
    }
}
