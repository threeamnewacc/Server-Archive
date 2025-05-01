package net.badlion.uhc.practice.kits;

import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class BuildKit extends Kit {

	public BuildKit() {
		super("builduhc", 1);
	}

	@Override
	public void giveItems(Player player) {
		// Clear items
		player.getInventory().clear();

		// Set items
		// TODO: Make it load from database!

		// Inv cleanup
		player.getInventory().setHeldItemSlot(0);
		player.updateInventory();
	}
}
