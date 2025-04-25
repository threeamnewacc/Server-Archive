package us.zonix.core.tab;

import org.bukkit.entity.Player;

public interface ITabHandler {

	/**
	 * This is called when the Players TabList has finished initialising.
	 *
	 * @param player  the player whose TabList has finished initialising
	 * @param time    the time it took to initialise in nanoseconds.
	 */
	void tabCreated(Player player, long time);
}
