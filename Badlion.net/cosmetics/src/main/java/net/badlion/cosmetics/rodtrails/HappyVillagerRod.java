package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class HappyVillagerRod extends RodTrail {

    public HappyVillagerRod() {
        super("happy_villager_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.EMERALD, ChatColor.GREEN + "Happy Villager Rod Trail",
                ChatColor.GRAY + "When you make a villager happy...", ChatColor.GRAY + "It makes you happy."));

        this.particleType = ParticleLibrary.ParticleType.VILLAGER_HAPPY;
    }
}
