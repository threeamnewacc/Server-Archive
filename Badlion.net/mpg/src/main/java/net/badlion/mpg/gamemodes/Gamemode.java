package net.badlion.mpg.gamemodes;

import net.badlion.mpg.kits.MPGKit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class Gamemode implements Listener {

	protected Random random = new Random();

	private static Map<String, Gamemode> gameModeMap = new HashMap<>();

    public Gamemode() {
        Gamemode.gameModeMap.put(this.getName(), this);
    }

	/**
	 * Get a tier item
	 */
	public abstract ItemStack getTierItem(int tier);

	/**
	 * Get common items for each tier
	 */
	public abstract List<ItemStack> getCommonTierItems(int tier);

	/**
	 * Get the number of tier random items
	 */
	public abstract int getNumOfTierRandom(int tier);

	/**
	 * Get the number of tier guaranteed items
	 */
	public abstract int getNumOfTierGuaranteed(int tier);

    /**
     * Do anything special for a death
     *
     * NOTE: Must be able to handle logger npcs.
     */
    public abstract void handleDeath(LivingEntity died);

    /**
     * Return name
     */
    public abstract String getName();

    /**
     * Default kit
     */
    public abstract MPGKit getDefaultKit();

    public static Gamemode getGamemode(String name) {
	    if (!Gamemode.gameModeMap.containsKey(name)) {
		    throw new RuntimeException("Invalid game mode requested " + name);
	    }

	    return Gamemode.gameModeMap.get(name);
    }

}