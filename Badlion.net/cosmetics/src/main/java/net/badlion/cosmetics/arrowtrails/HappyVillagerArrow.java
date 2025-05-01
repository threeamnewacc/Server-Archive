package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class HappyVillagerArrow extends ArrowTrail {

    public HappyVillagerArrow() {
        super("happy_villager_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.EMERALD, ChatColor.GREEN + "Happy Villager Arrow Trail",
                ChatColor.GRAY + "When you make a villager happy...", ChatColor.GRAY + "It makes you happy."));

        this.particleType = ParticleLibrary.ParticleType.VILLAGER_HAPPY;
    }
}
