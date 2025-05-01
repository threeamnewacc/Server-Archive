package net.badlion.capturetheflag.gamemodes;

import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.kits.MPGKit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;
                                       // TODO: MAYBE EXTEND CLASSICGAMEMODE INSTEAD? DEPENDS ON HOW YOU DO IT
public class MultipleFlagsGamemode extends Gamemode {

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
        return 0;
    }

    @Override
    public int getNumOfTierGuaranteed(int tier) {
        return 0;
    }

    @Override
    public void handleDeath(LivingEntity died) {
        // Call PlayerRespawnTask
    }

    @Override
    public String getName() {
        return "Multiple Flags";
    }

    @Override
    public MPGKit getDefaultKit() {
        return null;
    }

    public int getNumOfFlags() {
        return 2; // per team
    }

}
