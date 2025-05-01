package net.badlion.survivalgames.gamemodes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface GameMode {

    /**
     * Get a Tier 1 Item
     */
    public ItemStack getTier1Item();

    /**
     * Get a Tier 2 Item
     */
    public ItemStack getTier2Item();

    /**
     * Do anything special for a death
     */
    public void handleDeath(Player died);

    /**
     * Return name
     */
    public String name();


}
