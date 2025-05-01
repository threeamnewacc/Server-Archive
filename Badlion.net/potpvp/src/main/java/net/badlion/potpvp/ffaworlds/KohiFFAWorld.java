package net.badlion.potpvp.ffaworlds;

import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class KohiFFAWorld extends FFAWorld {

    public KohiFFAWorld(ItemStack ffaItem, KitRuleSet kitRuleSet) {
        super(ffaItem, kitRuleSet);
    }

    @Override
    public void startGame() {
        this.spawn = new Location(PotPvP.getInstance().getServer().getWorld("world"), 200.5, 35.5, -4861.5);
    }

}
