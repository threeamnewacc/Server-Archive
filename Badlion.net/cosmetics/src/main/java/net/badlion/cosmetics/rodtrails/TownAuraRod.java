package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class TownAuraRod extends RodTrail {

    public TownAuraRod() {
        super("aura_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.GOLD_INGOT, ChatColor.GREEN + "Aura Rod Trail",
                ChatColor.GRAY + "A mysterious aura.", ChatColor.GRAY + "They call it, the Town Aura."));

        this.particleType = ParticleLibrary.ParticleType.TOWN_AURA;

        this.amount = 5;
    }
}
