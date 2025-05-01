package net.badlion.cosmetics.pets;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;

public class UndeadHorsePet extends HorsePet {

    public UndeadHorsePet() {
        super("undead_horse", ItemRarity.RARE, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 100, ChatColor.GREEN + "Undead Horse", ChatColor.GRAY + "Ride a zombie horse!"));

        this.defaultDisplayName = ChatColor.RED + "%player%'s Undead Horse Pet";
        this.variant = Horse.Variant.UNDEAD_HORSE;
        this.permission = "badlion.pets.undeadhorse";
    }

}
