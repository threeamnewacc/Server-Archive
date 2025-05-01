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
import org.bukkit.entity.Slime;

public class SlimePet extends Pet {

    public SlimePet() {
        super("slime", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 55, ChatColor.GREEN + "Slime", ChatColor.GRAY + "These guys love to bounce!"));
        this.permission = "badlion.pets.slime";
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Slime slime = (Slime) this.handlePetInitialization(player, EntityType.SLIME, cosmeticsSettings);
        slime.setSize(1);

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SLIME_WALK", "ENTITY_SLIME_JUMP"), 1F, 1F);
        PetManager.addSpawnedPet(player, slime);

        return slime;
    }

    @Override
    public void despawn(Player player) {

    }

}
