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
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;

public class CowPet extends Pet {

    public CowPet() {
        super("baby_cow", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 92, ChatColor.GREEN + "Baby Cow", ChatColor.GRAY + "He's a bit cute."));

        this.permission = "badlion.pets.cow";
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        MushroomCow cow = (MushroomCow) this.handlePetInitialization(player, EntityType.MUSHROOM_COW, cosmeticsSettings);
        cow.setAgeLock(true);
        cow.setBaby();

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "COW_IDLE", "ENTITY_COW_AMBIENT"), 1F, 1F);
        PetManager.addSpawnedPet(player, cow);

        return cow;
    }

    @Override
    public void despawn(Player player) {

    }
}
