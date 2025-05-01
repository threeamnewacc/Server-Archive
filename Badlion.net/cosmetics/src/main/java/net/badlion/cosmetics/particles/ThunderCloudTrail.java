package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ThunderCloudTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.VILLAGER_ANGRY, 0, 1, 0);

    public ThunderCloudTrail() {
        super("thunder_cloud_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.BEACON, ChatColor.GREEN + "Thunder Cloud Trail", ChatColor.GRAY + "Make sure not to wear", ChatColor.GRAY + "conductive clothing!"));
    }

    @Override
    public void spawnParticle(Player player) {
        this.particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
