package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LoveTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.HEART, 0, 1, 0);

    public LoveTrail() {
        super("love_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.INK_SACK, (short) 1, ChatColor.GREEN + "Love Trail",
                ChatColor.GRAY + "Show your love for Badlion...", ChatColor.GRAY + "Or someone else!"));
    }

    @Override
    public void spawnParticle(Player player) {
        this.particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
