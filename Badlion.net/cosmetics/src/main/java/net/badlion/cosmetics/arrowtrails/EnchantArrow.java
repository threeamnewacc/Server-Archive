package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class EnchantArrow extends ArrowTrail {

    public EnchantArrow() {
        super("enchanted_arrow_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.ENCHANTMENT_TABLE, ChatColor.GREEN + "Enchanted Arrow Trail",
                ChatColor.GRAY + "Level 100 enchants, OP!"));

        this.particleType = ParticleLibrary.ParticleType.ENCHANTMENT_TABLE;
    }
}
