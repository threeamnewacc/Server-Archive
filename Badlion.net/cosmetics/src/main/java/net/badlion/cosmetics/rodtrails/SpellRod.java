package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class SpellRod extends RodTrail {

    public SpellRod() {
        super("spell_rod_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.POTION, ChatColor.GREEN + "Spell Rod Trail",
                ChatColor.GRAY + "Cast spells on your opponents", ChatColor.GRAY + "with the Spell arrow trail!"));

        this.particleType = ParticleLibrary.ParticleType.SPELL_WITCH;
    }
}
