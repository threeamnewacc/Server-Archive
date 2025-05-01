package net.badlion.uhcmeetup.gamemodes;

import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.kits.MPGKit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ClassicGamemode extends Gamemode {

	@Override
	public ItemStack getTierItem(int tier) {
		return null;
	}

	@Override
	public List<ItemStack> getCommonTierItems(int tier) {
		return null;
	}

	@Override
	public int getNumOfTierRandom(int tier) {
		return -1;
	}

	@Override
	public int getNumOfTierGuaranteed(int tier) {
		return -1;
	}

    @Override
    public void handleDeath(LivingEntity died) {

    }

	public String getName() {
		return "Classic";
	}

	@Override
	public MPGKit getDefaultKit() {
		return null;
	}

}
