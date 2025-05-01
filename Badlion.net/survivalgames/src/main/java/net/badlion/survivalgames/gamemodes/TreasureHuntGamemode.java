package net.badlion.survivalgames.gamemodes;

import org.bukkit.inventory.ItemStack;

public class TreasureHuntGamemode extends ClassicGamemode {

	@Override
	public ItemStack getTierItem(int tier) {
		// Tier 1 -> Tier 2, Tier 2 -> Tier 3
		int newTier = tier + 1;

		// Check in case we go over tier 3 (for supply drops)
		if (newTier > 3) newTier = 3;

		return super.getTierItem(newTier);
	}

	public String getName() {
		return "TreasureHunt";
	}

}
