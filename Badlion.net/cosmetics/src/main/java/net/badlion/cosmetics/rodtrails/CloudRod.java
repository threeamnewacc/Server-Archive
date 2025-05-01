package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;

public class CloudRod extends RodTrail {

    public CloudRod() {
        super("cloud_rod_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.WEB, ChatColor.GREEN + "Cloud Rod Trail",
                Collections.singletonList(ChatColor.GRAY + "Angels live in the clouds..")));

        this.particleType = ParticleLibrary.ParticleType.CLOUD;
    }
}
