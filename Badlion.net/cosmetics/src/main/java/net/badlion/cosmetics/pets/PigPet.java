package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

public class PigPet extends Pet {

    public PigPet() {
        super("pig", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 90, ChatColor.GREEN + "Pig", ChatColor.GRAY + "Oink!"));

        this.permission = "badlion.pets.pig";
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Pig pig = (Pig) this.handlePetInitialization(player, EntityType.PIG, cosmeticsSettings);
        pig.setBreed(false);
        pig.setAdult();
        pig.setSaddle(true);
        pig.setPassenger(player);

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "PIG_IDLE", "ENTITY_PIG_AMBIENT"), 1F, 1F);
        PetManager.addSpawnedPet(player, pig);

        return pig;
    }

    @Override
    public void despawn(Player player) {

    }

    @Override
    public void run(Player player, LivingEntity entity) {
        Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(2.0D));
        this.walkTo(entity, loc.getX(), loc.getY(), loc.getZ(), 1.2D);
    }
}
