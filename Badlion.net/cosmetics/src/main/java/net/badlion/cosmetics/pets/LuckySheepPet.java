package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LuckySheepPet extends Pet {

    public LuckySheepPet() {
        super("lucky_sheep", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 91, ChatColor.GREEN + "Lucky Sheep", ChatColor.GRAY + "This sheep is so lucky it makes emeralds!"));
        this.permission = "badlion.pets.luckysheep";
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Sheep sheep = (Sheep) this.handlePetInitialization(player, EntityType.SHEEP, cosmeticsSettings);
        sheep.setBaby();
        sheep.setBreed(false);
        //sheep.setAge(-1);
        sheep.setAgeLock(true);
        sheep.setColor(DyeColor.LIME);

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SHEEP_IDLE", "ENTITY_SHEEP_AMBIENT"), 1F, 1F);

        // Emerald poop task
        sheep.setMetadata("smelly_pet_task", new FixedMetadataValue(Cosmetics.getInstance(),
                new LuckySheepEmeraldDropTask(sheep).runTaskTimer(Cosmetics.getInstance(), 60L, 60L)));

        PetManager.addSpawnedPet(player, sheep);

        return sheep;
    }

    @Override
    public void despawn(Player player) {

    }

    public class LuckySheepEmeraldDropTask extends BukkitRunnable {

        private Sheep sheep;

        public LuckySheepEmeraldDropTask(Sheep sheep) {
            this.sheep = sheep;
        }

        @Override
        public void run() {
            if (Math.random() >= 0.65D) {
                Item item = this.sheep.getWorld().dropItemNaturally(this.sheep.getLocation(), new ItemStack(Material.EMERALD));
                item.setVelocity(new Vector(0D, 0.1D, 0D));
                item.setAge(5900);

                item.setPickupDelay(Integer.MAX_VALUE);
            }
        }

    }

}
