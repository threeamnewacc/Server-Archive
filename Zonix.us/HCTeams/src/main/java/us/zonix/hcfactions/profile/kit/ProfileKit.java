package us.zonix.hcfactions.profile.kit;

import lombok.Setter;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.enchantmentlimiter.EnchantmentLimiter;
import us.zonix.hcfactions.profile.kit.ability.ProfileKitAbility;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import us.zonix.hcfactions.util.InventorySerialisation;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.profile.kit.ability.ProfileKitAbility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ProfileKit {

    DIAMOND("Diamond", Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            new ProfileKitPotionEffect[0],
            new ProfileKitAbility[0]
    ),
    MINER("Miner", Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            new ProfileKitPotionEffect[]{
                    new ProfileKitPotionEffect(PotionEffectType.FAST_DIGGING, 2),
                    new ProfileKitPotionEffect(PotionEffectType.INVISIBILITY, 1),
                    new ProfileKitPotionEffect(PotionEffectType.NIGHT_VISION, 1)
            },
            new ProfileKitAbility[]{ProfileKitAbility.MINING_INVISIBILITY}
    ),
    ARCHER("Archer", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
            new ProfileKitPotionEffect[]{
                    new ProfileKitPotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2),
                    new ProfileKitPotionEffect(PotionEffectType.SPEED, 3),
            },
            new ProfileKitAbility[]{ProfileKitAbility.SINGLE_SUGAR_SPEED}
    ),
    ROGUE("Rogue", Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
            new ProfileKitPotionEffect[]{
                    new ProfileKitPotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1),
                    new ProfileKitPotionEffect(PotionEffectType.SPEED, 3),
            },
            new ProfileKitAbility[]{ProfileKitAbility.ROGUE_SUGAR_SPEED, ProfileKitAbility.ROGUE_FEATHER_JUMP}
    ),
    BARD("Bard", Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS,
            new ProfileKitPotionEffect[]{
                    new ProfileKitPotionEffect(PotionEffectType.REGENERATION, 2),
                    new ProfileKitPotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2),
                    new ProfileKitPotionEffect(PotionEffectType.SPEED, 2),
                    new ProfileKitPotionEffect(PotionEffectType.SPEED, 2)
            },
            new ProfileKitAbility[]{ProfileKitAbility.MULTI_SUGAR_SPEED, ProfileKitAbility.MULTI_FEATHER_JUMP, ProfileKitAbility.MULTI_IRON_RESISTANCE, ProfileKitAbility.MULTI_BLAZE_STRENGTH, ProfileKitAbility.MULTI_SPIDER_WITHER, ProfileKitAbility.MULTI_CREAM_FIRERES, ProfileKitAbility.MULTI_TEAR_REGEN});

    @Getter private String name;
    @Getter private Material helmet;
    @Getter private Material chestplate;
    @Getter private Material leggings;
    @Getter private Material boots;
    @Getter private ProfileKitAbility[] abilities;
    @Getter private ProfileKitPotionEffect[] potionEffects;

    ProfileKit(String name, Material helmet, Material chestplate, Material leggings, Material boots, ProfileKitPotionEffect[] potionEffects, ProfileKitAbility[] abilities) {
        this.name = name;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.potionEffects = potionEffects;
        this.abilities = abilities;
    }

    public boolean isWearingArmor(Player player) {
        PlayerInventory inventory = player.getInventory();
        return  (inventory.getHelmet() != null && inventory.getHelmet().getType() == helmet && inventory.getChestplate() != null && inventory.getChestplate().getType() == chestplate && inventory.getLeggings() != null && inventory.getLeggings().getType() == leggings && inventory.getBoots() != null && inventory.getBoots().getType() == boots);
    }

    public boolean hasAbility(ProfileKitAbility toCheck) {
        for (ProfileKitAbility ability : abilities) {
            if (ability == toCheck) {
                return true;
            }
        }
        return false;
    }

    public ItemStack[] getEnchantedArmor() {
        ItemStack[] items = new ItemStack[]{new ItemStack(getBoots()), new ItemStack(getLeggings()), new ItemStack(getChestplate()), new ItemStack(getHelmet()), new ItemStack(Material.DIAMOND_SWORD)};

        for (int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];
            if (itemStack == null) continue;
            for (Enchantment enchantment : Enchantment.values()) {
                if (enchantment.canEnchantItem(itemStack) && EnchantmentLimiter.getInstance().getEnchantmentLimit(enchantment) != enchantment.getMaxLevel()) {
                    if (EnchantmentLimiter.getInstance().getEnchantmentLimit(enchantment) > 0) {
                        itemStack.addEnchantment(enchantment, EnchantmentLimiter.getInstance().getEnchantmentLimit(enchantment));
                    }
                }
            }
        }

        return items;
    }

}
