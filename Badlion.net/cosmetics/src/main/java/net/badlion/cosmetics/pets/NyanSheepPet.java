package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

public class NyanSheepPet extends Pet {

    public NyanSheepPet() {
        super("nyan_sheep", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 91, ChatColor.GREEN + "Nyan Sheep", ChatColor.GRAY + "Mmmmmm, so colorful!"));
        this.permission = "badlion.pets.nyansheep";
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Sheep sheep = (Sheep) this.handlePetInitialization(player, EntityType.SHEEP, cosmeticsSettings);
        sheep.setBreed(false);
        sheep.setAgeLock(true);
        sheep.setCustomName("jeb_");

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SHEEP_IDLE", "ENTITY_SHEEP_AMBIENT"), 1F, 1F);
        PetManager.addSpawnedPet(player, sheep);

        return sheep;
    }

    @Override
    public void despawn(Player player) {

    }

}
