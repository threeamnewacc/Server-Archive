package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FlameTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.FLAME, 0, 1, 0);

    public FlameTrail() {
        super("flame_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.FIRE, ChatColor.GREEN + "Flame Trail", ChatColor.GRAY + "Fire in the hole!"));
    }

    @Override
    public void spawnParticle(Player player) {
        this.particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
