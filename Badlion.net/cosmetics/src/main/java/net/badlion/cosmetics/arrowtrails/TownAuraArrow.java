package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class TownAuraArrow extends ArrowTrail {

    public TownAuraArrow() {
        super("aura_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.GOLD_INGOT, ChatColor.GREEN + "Aura Arrow Trail",
                ChatColor.GRAY + "A mysterious aura.", ChatColor.GRAY + "They call it, the Town Aura."));

        this.particleType = ParticleLibrary.ParticleType.TOWN_AURA;

        this.amount = 5;
    }
}
