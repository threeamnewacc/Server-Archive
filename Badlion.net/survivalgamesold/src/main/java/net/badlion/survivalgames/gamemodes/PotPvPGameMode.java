package net.badlion.survivalgames.gamemodes;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class PotPvPGameMode implements GameMode, Listener {

    /**
     * Get a Tier 1 Item
     */
    public ItemStack getTier1Item() {
        return null;
    }

    /**
     * Get a Tier 2 Item
     */
    public ItemStack getTier2Item() {
        return null;
    }

    /**
     * Do anything special for a death
     */
    public void handleDeath(Player died) {

    }

    /**
     * Return name
     */
    public String name() {
        return "PotPVP";
    }

}
