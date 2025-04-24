package me.devkevin.landcore.player.grant;

import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.time.TimeFormatUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 0:47
 * Grant / land.pvp.core.player.grant / LandCore
 */
@Getter
@Setter
@AllArgsConstructor
public class Grant {
    private Rank rank;
    private long duration;
    private long addedAt;
    private String addedBy;
    private String reason;

    public boolean hasExpired() {
        if (duration == -1L) return false;

        return System.currentTimeMillis() > this.duration;
    }

    public String getNiceDuration() {
        if (duration == -1L) return "Never";
        return TimeFormatUtils.getDetailedTime(this.duration - System.currentTimeMillis());
    }
}
