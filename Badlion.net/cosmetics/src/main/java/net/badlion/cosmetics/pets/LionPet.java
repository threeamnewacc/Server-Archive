package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class LionPet extends Pet {

    public static boolean allowFireWorks = false; // FALSE NOOB
    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.FLAME, 0, 1, 0);

    public LionPet() {
        super("lion", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 95, ChatColor.GREEN + "Lion", ChatColor.GRAY + "Own a pet lion!"));
        this.permission = "badlion.pets.lion";
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Wolf wolf = (Wolf) this.handlePetInitialization(player, EntityType.WOLF, cosmeticsSettings);
        wolf.setBreed(false);
        wolf.setCollarColor(DyeColor.ORANGE);
        wolf.setBaby();
        wolf.setAgeLock(true);

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "WOLF_BARK", "ENTITY_WOLF_HOWL"), 1F, 1F);
        PetManager.addSpawnedPet(player, wolf);

        return wolf;
    }

    @Override
    public void despawn(Player player) {

    }

    @Override
    public void run(Player player, LivingEntity entity) {
        if (player.getLocation().distance(entity.getLocation()) >= 5) {
            entity.teleport(player);
        } else if (player.getLocation().distance(entity.getLocation()) >= 2) {
            this.walkTo(entity, player.getLocation().getX(), player.getLocation().getY(),
                    player.getLocation().getZ(), 1.0D);
        }
        this.particle.sendToLocation(player, entity.getLocation(), true, 0.3D, 5);
    }
}
