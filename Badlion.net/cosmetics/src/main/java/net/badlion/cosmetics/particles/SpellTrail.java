package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SpellTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.SPELL, 0, 1, 0);
    ;

    public SpellTrail() {
        super("spell_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.POTION, (short) 8195, ChatColor.GREEN + "Spell Trail", ChatColor.GRAY + "Magic, magic, MAGIC!!"));
    }

    @Override
    public void spawnParticle(Player player) {
        this.particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);
    }

}
