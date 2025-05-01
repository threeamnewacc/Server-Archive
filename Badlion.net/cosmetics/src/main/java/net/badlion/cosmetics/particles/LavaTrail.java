package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LavaTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.DRIP_LAVA, 0, 1, 0);

    public LavaTrail() {
        super("lava_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.LAVA_BUCKET, ChatColor.GREEN + "Lava Trail", ChatColor.GRAY + "Some people just like to watch", ChatColor.GRAY + "the world burn"));
    }

    @Override
    public void spawnParticle(Player player) {
        this.particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
