package us.zonix.hcfactions.profile.kit;

import us.zonix.hcfactions.FactionsPlugin;
import lombok.Getter;
import us.zonix.hcfactions.FactionsPlugin;

import java.text.DecimalFormat;

public class ProfileKitWarmup {

    public static final int DEFAULT_DURATION = FactionsPlugin.getInstance().getMainConfig().getInt("KIT.WARMUP.TIME");
    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");

    @Getter private final ProfileKit kit;
    @Getter private final long createdAt;
    @Getter private final long duration;

    public ProfileKitWarmup(ProfileKit kit, long duration) {
        this.kit = kit;
        this.createdAt = System.currentTimeMillis();
        this.duration = (duration * 1000) + 100;
    }

    public String getTimeLeft() {
        return SECONDS_FORMATTER.format((((createdAt + duration) - System.currentTimeMillis()) / 1000.0f));
    }

}
