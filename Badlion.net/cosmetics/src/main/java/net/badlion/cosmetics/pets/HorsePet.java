package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HorsePet extends Pet {

    protected String defaultDisplayName = "Horse";

    protected Horse.Variant variant;

    public HorsePet() {
        super("horse", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 100, ChatColor.GREEN + "Horse", ChatColor.GRAY + "Ride your own horse!"));

        this.variant = Horse.Variant.HORSE;
        this.permission = "badlion.pets.horse";
    }

    public HorsePet(String name, ItemRarity rarity, ItemStack itemStack) {
        super(name, rarity, itemStack);
    }

    @Override
    public LivingEntity spawnPet(final Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        final Horse horse = (Horse) this.handlePetInitialization(player, EntityType.HORSE, cosmeticsSettings);
        horse.setAdult();
        horse.setTamed(true);
        horse.setAgeLock(true);

        horse.setVariant(this.variant);

        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        //horse.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING));

        horse.setStyle(Horse.Style.values()[Gberry.generateRandomInt(0, Horse.Style.values().length - 1)]);
        horse.setColor(Horse.Color.values()[Gberry.generateRandomInt(0, Horse.Color.values().length - 1)]);

        if (horse.getJumpStrength() < 0.8D) horse.setJumpStrength(0.8D);

        horse.setMaxHealth(40D);
        horse.setHealth(40D);
        horse.setSpeed(0.25D);

        horse.setOwner(player);

        BukkitUtil.runTaskNextTick(new Runnable() {
            @Override
            public void run() {
                horse.setPassenger(player);
            }
        });

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "HORSE_IDLE", "ENTITY_HORSE_AMBIENT"), 1F, 1F);
        PetManager.addSpawnedPet(player, horse);

        return horse;
    }

    @Override
    public void despawn(Player player) {

    }

}
