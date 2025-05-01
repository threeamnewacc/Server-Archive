package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SnowTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.SNOWBALL, 0, 1, 0);

    public SnowTrail() {
        super("snow_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.SNOW_BALL, ChatColor.GREEN + "Snow Trail", ChatColor.GRAY + "Do you wanna build a snowman?"));
        this.speed = 2;
    }

    @Override
    public void spawnParticle(Player player) {
        this.particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
