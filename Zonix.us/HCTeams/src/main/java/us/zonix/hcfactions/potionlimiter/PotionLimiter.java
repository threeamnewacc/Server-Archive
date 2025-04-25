package us.zonix.hcfactions.potionlimiter;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.FactionsPlugin;

public class PotionLimiter {

    private static final PotionLimiter instance = new PotionLimiter();
    private static FactionsPlugin main = FactionsPlugin.getInstance();

    public boolean isBlocked(int data) {
        return main.getMainConfig().getStringList("BLOCKED_POTIONS").contains(data + "");
    }

    public static PotionLimiter getInstance() {
        return instance;
    }

}
