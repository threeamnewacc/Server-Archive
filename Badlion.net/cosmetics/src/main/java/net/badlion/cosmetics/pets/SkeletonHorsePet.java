package net.badlion.cosmetics.pets;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;

public class SkeletonHorsePet extends HorsePet {

    public SkeletonHorsePet() {
        super("skeleton_horse", ItemRarity.RARE, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 100, ChatColor.GREEN + "Skeleton Horse", ChatColor.GRAY + "Ride a skeleton horse!"));

        this.defaultDisplayName = ChatColor.RED + "%player%'s Skeleton Horse Pet";
        this.variant = Horse.Variant.SKELETON_HORSE;
        this.permission = "badlion.pets.skeletonhorse";
    }

}
