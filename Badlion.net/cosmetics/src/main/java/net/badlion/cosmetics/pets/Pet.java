package net.badlion.cosmetics.pets;

import net.badlion.cosmetics.CosmeticItem;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public abstract class Pet extends CosmeticItem {

    protected String permission;

    public Pet(String name, ItemRarity rarity, ItemStack itemStack) {
        super(Cosmetics.CosmeticType.PET, name, rarity, itemStack);
    }

    protected LivingEntity handlePetInitialization(Player player, EntityType entityType, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        LivingEntity livingEntity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation().add(0, 2, 0), entityType);

        livingEntity.setMetadata("smelly_pet_owner", new FixedMetadataValue(Cosmetics.getInstance(), player.getName()));
        livingEntity.setMetadata("smelly_pet_name", new FixedMetadataValue(Cosmetics.getInstance(), this.getName()));

        String petDisplayName = cosmeticsSettings.getPetDisplayName(this.getName());
        if (petDisplayName != null) {
            livingEntity.setCustomName(petDisplayName);
        } else {
            livingEntity.setCustomName(ChatColor.RED + player.getName() + "'s Pet");
        }

        return livingEntity;
    }

    public abstract LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings);

    public abstract void despawn(Player player);

    public void run(Player player, LivingEntity entity) {
        if (player.getLocation().distance(entity.getLocation()) >= 5) {
            entity.teleport(player);
        } else if (player.getLocation().distance(entity.getLocation()) >= 2) {
            this.walkTo(entity, player.getLocation().getX(), player.getLocation().getY(),
                    player.getLocation().getZ(), 1.2D);
        }
    }

    protected void walkTo(LivingEntity entity, double x, double y, double z, double speed) {
        Location to = new Location(entity.getWorld(), x, y, z);
        if (entity.getLocation().distance(to) >= 10) {
            // If they are too far away, just TP the entity
            entity.teleport(to);
        } else {
            // Otherwise just make the entity walk there
            entity.walkTo(x, y, z, speed);
        }
    }

    public ItemStack getItemStack(CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        String displayName = cosmeticsSettings.getPetDisplayName(this.getName());
        if (displayName != null) {
            // Put display name on item
            ItemStack itemStack = this.getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + displayName);

            // Add smelly pet name to lore so we can identify the item later
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + this.getName());
            itemMeta.setLore(lore);

            itemStack.setItemMeta(itemMeta);

            return itemStack;
        } else {
            return this.getItemStack();
        }
    }

    public String getPermission() {
        return permission == null ? "badlion.pets." + getName().toLowerCase().replace("_", "") : permission;
    }

}
