package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;

public class CloudArrow extends ArrowTrail {

    public CloudArrow() {
        super("cloud_arrow_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.WEB, ChatColor.GREEN + "Cloud Arrow Trail",
                Collections.singletonList(ChatColor.GRAY + "Angels live in the clouds..")));

        this.particleType = ParticleLibrary.ParticleType.CLOUD;
    }
}
