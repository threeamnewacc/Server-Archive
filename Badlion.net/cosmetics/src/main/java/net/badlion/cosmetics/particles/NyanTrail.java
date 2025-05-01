package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NyanTrail extends Particle {

    private ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.REDSTONE, 5, 1, 0);

    public NyanTrail() {
        super("nyan_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.WRITTEN_BOOK, ChatColor.GREEN + "Nyan Trail", ChatColor.GRAY + "Nyan, nyan, nyan, nyan..."));
    }

    @Override
    public void spawnParticle(Player player) {
        this.particleLibrary.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
