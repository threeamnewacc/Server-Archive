// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.inventory;

import org.bukkit.enchantments.Enchantment;
import org.json.simple.JSONObject;
import java.util.Iterator;
import java.util.List;
import club.minemen.practice.player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryClickEvent;
import club.minemen.core.util.finalutil.ItemUtil;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.Arrays;
import org.bukkit.Material;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.util.MathUtil;
import org.bukkit.potion.PotionEffect;
import java.util.ArrayList;
import club.minemen.practice.Practice;
import club.minemen.practice.match.Match;
import org.bukkit.entity.Player;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import club.minemen.core.inventory.InventoryUI;

public class InventorySnapshot
{
    private final InventoryUI inventoryUI;
    private final ItemStack[] originalInventory;
    private final ItemStack[] originalArmor;
    private final UUID snapshotId;
    
    public InventorySnapshot(final Player player, final Match match) {
        this.snapshotId = UUID.randomUUID();
        final ItemStack[] contents = player.getInventory().getContents();
        final ItemStack[] armor = player.getInventory().getArmorContents();
        this.originalInventory = contents;
        this.originalArmor = armor;
        final PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        final double health = player.getHealth();
        final double food = player.getFoodLevel();
        final List<String> potionEffectStrings = new ArrayList<String>();
        for (final PotionEffect potionEffect : player.getActivePotionEffects()) {
            final String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
            final String effectName = StringUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
            final String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());
            potionEffectStrings.add(CC.PRIMARY + effectName + " " + romanNumeral + CC.SECONDARY + " (" + duration + ")");
        }
        this.inventoryUI = new InventoryUI(player.getName(), true, 6);
        for (int i = 0; i < 9; ++i) {
            this.inventoryUI.setItem(i + 27, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(contents[i]));
            this.inventoryUI.setItem(i + 18, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(contents[i + 27]));
            this.inventoryUI.setItem(i + 9, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(contents[i + 18]));
            this.inventoryUI.setItem(i, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(contents[i + 9]));
        }
        boolean potionMatch = false;
        boolean soupMatch = false;
        for (final ItemStack item : match.getKit().getContents()) {
            if (item != null) {
                if (item.getType() == Material.MUSHROOM_SOUP) {
                    soupMatch = true;
                    break;
                }
                if (item.getType() == Material.POTION && item.getDurability() == 16421) {
                    potionMatch = true;
                    break;
                }
            }
        }
        if (potionMatch) {
            final int potCount = (int)Arrays.stream(contents).filter(Objects::nonNull).map((Function<? super ItemStack, ?>)ItemStack::getDurability).filter(d -> d == 16421).count();
            this.inventoryUI.setItem(47, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.POTION, CC.PRIMARY + "Health Potions: " + CC.SECONDARY + potCount, potCount, (short)16421), new String[] { CC.PRIMARY + "Missed Potions: " + CC.SECONDARY + playerData.getMissedPots() })));
        }
        else if (soupMatch) {
            final int soupCount = (int)Arrays.stream(contents).filter(Objects::nonNull).map((Function<? super ItemStack, ?>)ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();
            this.inventoryUI.setItem(47, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.MUSHROOM_SOUP, CC.PRIMARY + "Remaining Soups: " + CC.SECONDARY + soupCount, soupCount, (short)16421)));
        }
        this.inventoryUI.setItem(48, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.SKULL_ITEM, CC.PRIMARY + "Hearts: " + CC.SECONDARY + MathUtil.roundToHalves(health / 2.0) + " / 10 \u2764", (int)Math.round(health / 2.0))));
        this.inventoryUI.setItem(49, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.COOKED_BEEF, CC.PRIMARY + "Hunger: " + CC.SECONDARY + MathUtil.roundToHalves(food / 2.0) + " / 10 \u2764", (int)Math.round(food / 2.0))));
        this.inventoryUI.setItem(50, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.BREWING_STAND_ITEM, CC.PRIMARY + "Potion Effects", potionEffectStrings.size()), (String[])potionEffectStrings.toArray(new String[0]))));
        this.inventoryUI.setItem(51, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.DIAMOND_SWORD, CC.PRIMARY + "Statistics"), new String[] { CC.PRIMARY + "Longest Combo: " + CC.SECONDARY + playerData.getLongestCombo() + " Hit" + ((playerData.getLongestCombo() > 1) ? "s" : ""), CC.PRIMARY + "Total Hits: " + CC.SECONDARY + playerData.getHits() + " Hit" + ((playerData.getHits() > 1) ? "s" : "") })));
        if (!match.isParty()) {
            for (int j = 0; j < 2; ++j) {
                this.inventoryUI.setItem((j == 0) ? 53 : 45, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.PAPER, CC.PRIMARY + "View Other Inventory"), new String[] { CC.PRIMARY + "Click to view the other inventory" })) {
                    public void onClick(final InventoryClickEvent inventoryClickEvent) {
                        final Player clicker = (Player)inventoryClickEvent.getWhoClicked();
                        if (Practice.getInstance().getMatchManager().isRematching(player.getUniqueId())) {
                            clicker.closeInventory();
                            Practice.getInstance().getServer().dispatchCommand((CommandSender)clicker, "inv " + Practice.getInstance().getMatchManager().getRematcherInventory(player.getUniqueId()));
                        }
                    }
                });
            }
        }
        for (int j = 36; j < 40; ++j) {
            this.inventoryUI.setItem(j, (InventoryUI.ClickableItem)new InventoryUI.EmptyClickableItem(armor[39 - j]));
        }
    }
    
    public JSONObject toJson() {
        final JSONObject object = new JSONObject();
        final JSONObject inventoryObject = new JSONObject();
        for (int i = 0; i < this.originalInventory.length; ++i) {
            inventoryObject.put((Object)i, (Object)this.encodeItem(this.originalInventory[i]));
        }
        object.put((Object)"inventory", (Object)inventoryObject);
        final JSONObject armourObject = new JSONObject();
        for (int j = 0; j < this.originalArmor.length; ++j) {
            armourObject.put((Object)j, (Object)this.encodeItem(this.originalArmor[j]));
        }
        object.put((Object)"armour", (Object)armourObject);
        return object;
    }
    
    private JSONObject encodeItem(final ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }
        final JSONObject object = new JSONObject();
        object.put((Object)"material", (Object)itemStack.getType().name());
        object.put((Object)"durability", (Object)itemStack.getDurability());
        object.put((Object)"amount", (Object)itemStack.getAmount());
        final JSONObject enchants = new JSONObject();
        for (final Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            enchants.put((Object)enchantment.getName(), itemStack.getEnchantments().get(enchantment));
        }
        object.put((Object)"enchants", (Object)enchants);
        return object;
    }
    
    public InventoryUI getInventoryUI() {
        return this.inventoryUI;
    }
    
    public ItemStack[] getOriginalInventory() {
        return this.originalInventory;
    }
    
    public ItemStack[] getOriginalArmor() {
        return this.originalArmor;
    }
    
    public UUID getSnapshotId() {
        return this.snapshotId;
    }
}
