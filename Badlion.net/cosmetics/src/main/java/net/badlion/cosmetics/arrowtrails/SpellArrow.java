package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class SpellArrow extends ArrowTrail {

    public SpellArrow() {
        super("spell_arrow_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.POTION, ChatColor.GREEN + "Spell Arrow Trail",
                ChatColor.GRAY + "Cast spells on your opponents", ChatColor.GRAY + "with the Spell arrow trail!"));

        this.particleType = ParticleLibrary.ParticleType.SPELL_WITCH;
    }
}
