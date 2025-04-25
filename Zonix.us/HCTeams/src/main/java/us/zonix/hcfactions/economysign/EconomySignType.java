package us.zonix.hcfactions.economysign;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.FactionsPlugin;

import java.util.List;

public enum EconomySignType {
    BUY,
    SELL;

    private static FactionsPlugin main = FactionsPlugin.getInstance();

    public List<String> getSignText() {
        return main.getLanguageConfig().getStringList("ECONOMY.SIGN." + name() + "_TEXT");
    }

}
