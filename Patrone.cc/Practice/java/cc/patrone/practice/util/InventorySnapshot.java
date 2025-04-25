package cc.patrone.practice.util;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventorySnapshot {

    private Inventory inventory;

    private Player opponent;
    private static HashMap<UUID, InventorySnapshot> inv = new HashMap();

    public InventorySnapshot(final Player player, Player opponent) {
        this.opponent = opponent;
        PracticeProfile practiceProfile = PracticePlugin.getInstance().getManagerHandler().getProfileManager().getProfile(player);
        final ItemStack[] contents1 = player.getInventory().getContents();
        final ItemStack[] armor1 = player.getInventory().getArmorContents();
        List<ItemStack> contents = new ArrayList<>();
        List<ItemStack> armor = new ArrayList<>();
        for (int i = 0; i < contents1.length; i++) {
            ItemStack itemStack = contents1[i];
            contents.add(i, itemStack);
        }
        for (int i = 0; i < armor1.length; i++) {
            ItemStack itemStack = armor1[i];
            armor.add(i, itemStack);
        }

        this.inventory = Bukkit.createInventory(null, 54, ChatColor.GRAY + "Inventory of " + player.getName());
        final int potCount = (int) Arrays.stream(contents1).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();
        final double health = ((Damageable) player).getHealth();
        for (int i = 0; i < 9; ++i) {
            if (contents.size() >= (i + 27) + 1) {
                this.inventory.setItem(i + 27, contents.get(i));
                this.inventory.setItem(i + 18, contents.get(i + 27));
                this.inventory.setItem(i + 9, contents.get(i + 18));
                this.inventory.setItem(i, contents.get(i + 9));
            }
        }
        this.inventory.setItem(48, ItemUtil.createItem(Material.SKULL_ITEM, ChatColor.YELLOW + "Hearts: " + ChatColor.GOLD + Math.round(health / 2) + " / 10" + ChatColor.RED + " \u2764", ((int) Math.round(health / 2.0) < 1 ? 1 : (int) Math.round(health / 2.0))));
        this.inventory.setItem(50, ItemUtil.createItem(Material.POTION, ChatColor.YELLOW + "Pots: " + ChatColor.GOLD + potCount + " \u2764", (potCount < 1 ? 1 : potCount), (short) 16421));
        double multiplier = 100 / (practiceProfile.getThrownPots() > 0 ? practiceProfile.getThrownPots() : 1);
        double potAccuracy = (multiplier * practiceProfile.getFullyLandedPots());
        if (potAccuracy > 100) {
            potAccuracy = 100;
        }
        ItemStack item = ItemUtil.createItem(Material.GOLD_SWORD, ChatColor.GOLD + "Match Statistics:", 1);
        ItemUtil.reloreItem(item, ChatColor.YELLOW + "Longest combo: " + ChatColor.GOLD + practiceProfile.getLongestCombo(), ChatColor.YELLOW + "Hit" + (practiceProfile.getHits() > 0 ? "s: " : ": ") + ChatColor.GOLD + practiceProfile.getHits(), ChatColor.YELLOW + "Pot accuracy: " + ChatColor.GOLD + Math.round(potAccuracy) + "%");
        this.inventory.setItem(49, item);
        for (int i = 0; i < 4; ++i) {
            if (contents.size() >= i) {
                this.inventory.setItem(39 - i, armor.get(i));
            }
            inventory.setItem(53, new ItemBuilder(Material.LEVER).setName(ChatColor.GRAY + "View " + opponent.getName() + "'s Inventory").toItemStack());
            inv.put(player.getUniqueId(), this);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public static InventorySnapshot getByPlayer(Player p) {
        return inv.get(p.getUniqueId());
    }

    public Player getOpponent() {
        return opponent;
    }
}
