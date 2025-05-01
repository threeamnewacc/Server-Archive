package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PortalTrail extends Particle {

    private ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.PORTAL, 0, 1, 0);

    public PortalTrail() {
        super("portal_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.PORTAL, ChatColor.GREEN + "Portal Trail",
                ChatColor.GRAY + "It's like travelling to the nether...", ChatColor.GRAY + "But not!"));
    }

    @Override
    public void spawnParticle(Player player) {
        this.particleLibrary.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
