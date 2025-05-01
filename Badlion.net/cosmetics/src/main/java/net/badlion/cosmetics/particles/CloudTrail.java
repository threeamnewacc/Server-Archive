package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CloudTrail extends Particle {

    private ParticleLibrary cloud = new ParticleLibrary(ParticleLibrary.ParticleType.CLOUD, 0.0D, 10, 0.4D);
    private ParticleLibrary waterDrip = new ParticleLibrary(ParticleLibrary.ParticleType.DRIP_WATER, 0.0D, 1, 0.35D);

    public CloudTrail() {
        super("cloud_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.INK_SACK, (short) 15, ChatColor.GREEN + "Cloud Trail",
                Arrays.asList(ChatColor.GRAY + "Been very unlucky recently?", ChatColor.GRAY + "Show everyone with the Cloud Trail!")));
    }

    @Override
    public void spawnParticle(final Player player) {
        Location location = player.getLocation().add(0.0D, 4.6D, 0.0D);

        this.cloud.sendToLocation(player, location, 2);
        this.waterDrip.sendToLocation(player, location);
    }

}
