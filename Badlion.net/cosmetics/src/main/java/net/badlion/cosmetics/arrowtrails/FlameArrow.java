package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;

public class FlameArrow extends ArrowTrail {

    public FlameArrow() {
        super("flame_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.FIRE, ChatColor.GREEN + "Flame Arrow Trail",
                Collections.singletonList(ChatColor.GRAY + "Fire awayyy... Fire away!")));

        this.particleType = ParticleLibrary.ParticleType.FLAME;

        this.speed = 1;
    }
}
