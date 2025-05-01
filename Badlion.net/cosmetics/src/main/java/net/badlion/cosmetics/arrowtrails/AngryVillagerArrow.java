package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class AngryVillagerArrow extends ArrowTrail {

    public AngryVillagerArrow() {
        super("angry_villager_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.DIAMOND, ChatColor.GREEN + "Angry Villager Arrow Trail",
                ChatColor.GRAY + "Ever made a villager angry?", ChatColor.GRAY + "Well now you have!"));

        this.particleType = ParticleLibrary.ParticleType.VILLAGER_ANGRY;
    }
}
