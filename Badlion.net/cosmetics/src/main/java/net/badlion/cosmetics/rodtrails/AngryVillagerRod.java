package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class AngryVillagerRod extends RodTrail {

    public AngryVillagerRod() {
        super("angry_villager_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.DIAMOND, ChatColor.GREEN + "Angry Villager Rod Trail",
                ChatColor.GRAY + "Ever made a villager angry?", ChatColor.GRAY + "Well now you have!"));

        this.particleType = ParticleLibrary.ParticleType.VILLAGER_ANGRY;
    }
}
