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
import org.bukkit.entity.Spider;

public class SpiderPet extends Pet {

    public SpiderPet() {
        super("spider", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, EntityType.SPIDER.getTypeId(), ChatColor.GREEN + "Spider", ChatColor.GRAY + "Creepy crawly"));

        // Permission
        this.permission = "badlion.pets.spider";
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Spider spider = (Spider) this.handlePetInitialization(player, EntityType.SPIDER, cosmeticsSettings);

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CHICKEN_IDLE", "ENTITY_CHICKEN_AMBIENT"), 1F, 1F);
        PetManager.addSpawnedPet(player, spider);

        return spider;
    }

    @Override
    public void despawn(Player player) {

    }

}
