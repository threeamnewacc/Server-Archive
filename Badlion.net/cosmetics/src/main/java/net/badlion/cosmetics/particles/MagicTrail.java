package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MagicTrail extends Particle {

    private ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.SPELL_WITCH, 0, 1, 0);

    public MagicTrail() {
        super("magic_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.EYE_OF_ENDER, ChatColor.GREEN + "Magic Trail", ChatColor.GRAY + "Do you have the magic in you?"));
    }

    @Override
    public void spawnParticle(Player player) {
        this.particleLibrary.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
