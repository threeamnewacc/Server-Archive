package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ArcTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.EXPLOSION_LARGE, 0, 1, 0);
    ;

    public ArcTrail() {
        super("arc_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.DIAMOND, ChatColor.GREEN + "Arc Trail", ChatColor.GRAY + "Explosive arc..."));
        this.speed = 8;

    }

    @Override
    public void spawnParticle(Player player) {
        this.particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
