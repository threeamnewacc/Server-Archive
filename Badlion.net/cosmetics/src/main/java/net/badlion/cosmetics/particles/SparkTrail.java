package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SparkTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.FIREWORKS_SPARK, 0, 1, 0);

    public SparkTrail() {
        super("spark_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Spark Trail", ChatColor.GRAY + "Harness the firework spark", ChatColor.GRAY + "and control it around you."));
    }

    @Override
    public void spawnParticle(Player player) {
        this.particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
