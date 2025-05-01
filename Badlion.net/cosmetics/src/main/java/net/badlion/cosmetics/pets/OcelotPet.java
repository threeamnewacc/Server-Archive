package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

public class OcelotPet extends Pet {

    int i = 0;
    private ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.CRIT_MAGIC, 0, 1, 0);

    public OcelotPet() {
        super("ocelot", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 98, ChatColor.GREEN + "Ocelot", ChatColor.GRAY + "Some say the ocelot cannot stop", ChatColor.GRAY + "walking around it's owner..."));

        // Permission
        this.permission = "badlion.pets.ocelot";
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Ocelot ocelot = (Ocelot) this.handlePetInitialization(player, EntityType.OCELOT, cosmeticsSettings);
        ocelot.setAgeLock(true);
        ocelot.setBaby();
        ocelot.clearAIGoals();

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CAT_MEOW", "ENTITY_CAT_PURREOW"), 1F, 1F);
        PetManager.addSpawnedPet(player, ocelot);

        return ocelot;
    }

    @Override
    public void despawn(Player player) {

    }

    @Override
    public void run(Player player, LivingEntity entity) {
        double angle, x, z;
        angle = 2 * Math.PI * this.i / 6;
        x = Math.cos(angle) * 1.5D;
        z = Math.sin(angle) * 1.5D;
        x += player.getLocation().getX();
        z += player.getLocation().getZ();
        this.i++;

        if (player.getLocation().distance(entity.getLocation()) >= 5) {
            entity.teleport(player);
        } else if (player.getLocation().distance(entity.getLocation()) >= 2) {
            this.walkTo(entity, x, player.getLocation().getY(), z, 1.2D);
        }
        this.particleLibrary.sendToLocation(player, entity.getLocation(), true, 0.3D, 1);
    }

}
