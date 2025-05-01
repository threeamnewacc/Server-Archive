package net.badlion.cosmetics;

import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.Gberry;
import net.badlion.smellycases.CaseItem;
import net.badlion.smellycases.CaseItemRarity;
import net.badlion.smellycases.CaseTier;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CosmeticItem extends CaseItem {

    protected boolean allowedForAllPermissions = true;
    private Cosmetics.CosmeticType cosmeticType;
    public CosmeticItem(Cosmetics.CosmeticType cosmeticType, String name, ItemRarity rarity, ItemStack itemStack) {
        super(name, itemStack, rarity, CaseTier.NORMAL, Gberry.ServerType.LOBBY);

        this.cosmeticType = cosmeticType;
        this.setPrizeName(this.cosmeticType.name().toLowerCase() + "-" + name);
    }

    @Override
    public void rewardPlayer(Player player) {
        player.sendMessage(ChatColor.DARK_GREEN + "You received " + ChatColor.GOLD + StringCommon.niceUpperCase(this.getName()) + ChatColor.DARK_GREEN + " in a case!");
        Cosmetics.getInstance().getServer().dispatchCommand(Cosmetics.getInstance().getServer().getConsoleSender(), "givecosmetic " + player.getName() + " " + this.cosmeticType + " " + this.getName());
    }

    public boolean isAllowedForAllPermissions() {
        return allowedForAllPermissions;
    }

    public enum ItemRarity implements CaseItemRarity {
        SUPER_COMMON(ChatColor.DARK_GREEN + "Super Common", 20),
        COMMON(ChatColor.GREEN + "Common", 16),
        UNCOMMON(ChatColor.YELLOW + "Uncommon", 12),
        RARE(ChatColor.DARK_PURPLE + "Rare", 4),
        SUPER_RARE(ChatColor.RED + "Super Rare", 2),
        LEGENDARY(ChatColor.AQUA + "Legendary", 1),
        SPECIAL(ChatColor.GOLD + ChatColor.BOLD.toString() + "Special", 0);

        private String name;
        private int rarity;

        ItemRarity(String name, int rarity) {
            this.name = name;
            this.rarity = rarity;
        }

        @Override
        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this.name;
        }

        public int getRarity() {
            return this.rarity;
        }

        public boolean isRare() {
            return this == ItemRarity.RARE;
        }

        public boolean isSuperRare() {
            return this == ItemRarity.SUPER_RARE;
        }

        public boolean isLegendary() {
            return this == ItemRarity.LEGENDARY;
        }

    }
}
