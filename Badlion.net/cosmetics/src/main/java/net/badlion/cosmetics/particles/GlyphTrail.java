package net.badlion.cosmetics.particles;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GlyphTrail extends Particle {

    public GlyphTrail() {
        super("glyph_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.EMPTY_MAP, ChatColor.GREEN + "Glyph Trail",
                Arrays.asList(ChatColor.GRAY + "There are many theories to the creation", ChatColor.GRAY + "of this mysterious trail...")));
    }

    @Override
    public void spawnParticle(Player player) {
        // Use the spigot method for more particle control
        player.getWorld().spigot().playEffect(Particle.getParticleLocation(player.getLocation(), 0.3D), Effect.FLYING_GLYPH, 0, 0,
                0.1337F, 0.1337F, 0.1337F, 0.05F, 9, 64);
    }

}
