package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class EnchantRod extends RodTrail {

    public EnchantRod() {
        super("enchanted_rod_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.ENCHANTMENT_TABLE, ChatColor.GREEN + "Enchanted Rod Trail",
                ChatColor.GRAY + "Level 100 enchants, OP!"));

        this.particleType = ParticleLibrary.ParticleType.ENCHANTMENT_TABLE;
    }
}
