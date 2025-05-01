package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SmokeTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.SMOKE_LARGE, 0, 1, 0);

    public SmokeTrail() {
        super("smoke_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.COAL, ChatColor.GREEN + "Smoke Trail",
                ChatColor.GRAY + "Trust me! It makes you look cool!"));
    }

    @Override
    public void spawnParticle(Player player) {
        particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
