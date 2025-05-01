package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ChickenPet extends Pet {

    public ChickenPet() {
        super("baby_chicken", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 93, ChatColor.GREEN + "Baby Chicken", ChatColor.GRAY + "Don't be a chicken!"));

        // Permission
        this.permission = "badlion.pets.chicken";
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Chicken chicken = (Chicken) this.handlePetInitialization(player, EntityType.CHICKEN, cosmeticsSettings);
        chicken.setAgeLock(true);
        chicken.setBaby();

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CHICKEN_IDLE", "ENTITY_CHICKEN_AMBIENT"), 1F, 1F);
        PetManager.addSpawnedPet(player, chicken);

        return chicken;
    }

    @Override
    public void despawn(Player player) {

    }

}
