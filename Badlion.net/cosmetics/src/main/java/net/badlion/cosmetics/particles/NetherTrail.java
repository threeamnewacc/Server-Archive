package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NetherTrail extends Particle {

    private ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.LAVA, 0, 1, 0);

    public NetherTrail() {
        super("nether_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.BLAZE_POWDER, ChatColor.GREEN + "Nether Trail", ChatColor.GRAY + "Release the hellish side in you..."));
    }

    @Override
    public void spawnParticle(Player player) {
        this.particleLibrary.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
