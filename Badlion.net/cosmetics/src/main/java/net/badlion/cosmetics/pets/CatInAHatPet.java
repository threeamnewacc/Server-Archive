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
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

public class CatInAHatPet extends Pet {

    public CatInAHatPet() {
        super("cat_in_a_hat", ItemRarity.COMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 98, ChatColor.GREEN + "Cat in a Hat", ChatColor.GRAY + "Keep your cat, as your hat!"));

        this.permission = "badlion.pets.catinahat";
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Ocelot ocelot = (Ocelot) this.handlePetInitialization(player, EntityType.OCELOT, cosmeticsSettings);
        ocelot.setBaby();
        ocelot.setTamed(true);
        ocelot.setBreed(false);
        ocelot.setSitting(true);
        ocelot.setAgeLock(true);
        ocelot.setCatType(Ocelot.Type.RED_CAT);
        //ocelot.setCatType(Ocelot.Type.values()[Gberry.generateRandomInt(0, Ocelot.Type.values().length - 1)]);

        player.setPassenger(ocelot);
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CAT_MEOW", "ENTITY_CAT_PURREOW"), 1F, 1F);
        PetManager.addSpawnedPet(player, ocelot);

        return ocelot;
    }

    @Override
    public void despawn(Player player) {

    }

    @Override
    public void run(Player player, LivingEntity entity) {
        //entity.teleport(player.getLocation().add(0.0D, 3.0D, 0.0D));
    }

    @Override
    public void walkTo(LivingEntity entity, double x, double y, double z, double speed) {
        // Do nothing
    }

}
