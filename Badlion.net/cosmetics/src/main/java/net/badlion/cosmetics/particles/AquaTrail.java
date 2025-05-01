package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AquaTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.WATER_DROP, 0, 1, 0);

    public AquaTrail() {
        super("aqua_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.WATER_BUCKET, ChatColor.GREEN + "Aqua Trail", ChatColor.GRAY + "So. Much. Water!"));
    }

    @Override
    public void spawnParticle(final Player player) {
        this.particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
