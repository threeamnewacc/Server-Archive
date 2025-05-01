package net.badlion.smellycases.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellycases.Case;
import net.badlion.smellycases.CaseItem;
import net.badlion.smellycases.CaseTier;
import net.badlion.smellycases.SmellyCases;
import net.badlion.smellycases.events.RequestPlayerOwnedCases;
import net.badlion.smellycases.tasks.InventoryScrollerTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CaseManager {

    public static Set<UUID> openingCases = new HashSet<>();
    private static ItemStack itemIndicator;
    private static List<ItemStack> fillerItems = new ArrayList<>();
    private static Map<Gberry.ServerType, Map<CaseTier, Map<CaseItem, Integer>>> caseItems = new HashMap<>();

    public static void initialize() {
        for (Gberry.ServerType serverType : Gberry.ServerType.values()) {
            CaseManager.caseItems.put(serverType, new HashMap<CaseTier, Map<CaseItem, Integer>>());
        }

        CaseManager.itemIndicator = Gberry.getGlowItem(ItemStackUtil.createItem(Material.NAME_TAG, ChatColor.WHITE.toString()));

        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 1, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 2, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 3, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 4, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 5, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 6, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 7, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 8, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 9, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 10, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 11, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 12, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 13, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 14, ChatColor.WHITE.toString()));
        CaseManager.fillerItems.add(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 15, ChatColor.WHITE.toString()));
    }

    public static void addCaseItem(CaseItem caseItem) {
        // Can't unlock this
        if (caseItem.getCaseItemRarity().getRarity() == 0) {
            return;
        }

        Map<CaseItem, Integer> items = CaseManager.caseItems.get(caseItem.getServerType()).get(caseItem.getTier());

        // Null checks
        if (items == null) {
            items = new LinkedHashMap<>(); // Keep the order
            CaseManager.caseItems.get(caseItem.getServerType()).put(caseItem.getTier(), items);
        }

        // Store mapping of case item to rarity
        items.put(caseItem, caseItem.getCaseItemRarity().getRarity());
    }

    public static void openCase(Player player, CaseTier tier, Gberry.ServerType serverType) {
        // Are they already opening a case?
        if (CaseManager.openingCases.contains(player.getUniqueId())) {
            InventoryScrollerTask runningTask = InventoryScrollerTask.getRunningTask(player);
            if (runningTask != null) { // Should never return null, but make IntelliJ happy
                BukkitUtil.openInventory(player, runningTask.getInventory());
                return;
            }
        }

        // Check to see if they have all items already
        if (player.hasPermission("badlion.allcosmetics") && serverType == Gberry.ServerType.LOBBY) {
            player.sendMessage(ChatColor.RED + "You have access to all cosmetics!");
            return;
        }

        Case caseToOpen = CaseDataManager.getNextCase(player.getUniqueId(), serverType);
        if (caseToOpen == null) {
            player.sendMessage(ChatColor.RED + "You do not have any " + serverType.getName() + " cases!");
            player.sendMessage(ChatColor.RED + "Purchase cases via the Badlion web-store here: http://store.badlion.net/category/666868");
            return;
        }

        if (hasAllCaseItems(player, tier, serverType)) {
            player.sendMessage(ChatColor.RED + "You already have all items for this case type!");
            return;
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Opening " + serverType.getName() + " case...");
        player.sendMessage("");

        Inventory inventory = CaseManager.getCaseInventory(player, tier, serverType, caseToOpen);

        BukkitUtil.openInventory(player, inventory);

        openingCases.add(player.getUniqueId());

        // Start the inventory scroller task
        new InventoryScrollerTask(player, inventory, tier, serverType, caseToOpen).runTaskTimer(SmellyCases.getInstance(), 2L, 2L);
    }

    private static Inventory getCaseInventory(Player player, CaseTier tier, Gberry.ServerType serverType, Case caseToOpen) {
        Inventory inventory = SmellyCases.getInstance().getServer().createInventory(new CaseHolder(), 27,
                ChatColor.AQUA + ChatColor.BOLD.toString() + "Opening " + serverType.getName() + " Case");

        // Add the indicator items
        inventory.setItem(4, CaseManager.itemIndicator);
        inventory.setItem(22, CaseManager.itemIndicator);

        // Add the filler items (2 for loops for increased performance)
        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                inventory.setItem(i, CaseManager.getRandomFillerItem());
            }
        }
        for (int i = 18; i < 26; i++) {
            if (i != 22) {
                inventory.setItem(i, CaseManager.getRandomFillerItem());
            }
        }

        // Add the initial rewards (rewards will be scrolling left to right)
        List<ItemStack> caseItemStacks = CaseManager.getRandomCaseItem(player, tier, serverType, caseToOpen, 9);
        for (int i = 9; i < 18; i++) {
            if (caseItemStacks.size() > i - 9) {
                inventory.setItem(i, caseItemStacks.get(i - 9));
            }
        }

        return inventory;
    }

    public static ItemStack getRandomFillerItem() {
        return CaseManager.fillerItems.get(Gberry.generateRandomInt(0, CaseManager.fillerItems.size() - 1));
    }

    public static boolean hasAllCaseItems(Player player, CaseTier tier, Gberry.ServerType serverType) {
        Map<CaseItem, Integer> items = CaseManager.caseItems.get(serverType).get(tier);

        for (CaseItem caseItem : items.keySet()) {
            if (!RequestPlayerOwnedCases.hasCaseItem(player, caseItem.getName())) {
                return false;
            }
        }

        return true;
    }

    public static ItemStack getRandomCaseItem(Player player, CaseTier tier, Gberry.ServerType serverType, Case caseToOpen) {
        List<ItemStack> itemStacks = CaseManager.getRandomCaseItem(player, tier, serverType, caseToOpen, 1);

        return itemStacks.size() == 0 ? null : itemStacks.get(0);
    }

    public static List<ItemStack> getRandomCaseItem(Player player, CaseTier tier, Gberry.ServerType serverType, Case caseToOpen, int numberOfItems) {
        List<ItemStack> caseItemStacks = new ArrayList<>();

        Map<CaseItem, Integer> items = CaseManager.caseItems.get(serverType).get(tier);
        Map<Integer, CaseItem> mapping = new LinkedHashMap<>();

        // Shuffle the case items to ensure true randomness
        ArrayList<CaseItem> shuffledItems = new ArrayList<>();
        shuffledItems.addAll(items.keySet());
        Collections.shuffle(shuffledItems);

        // Go through each case item and add it into our chance factor
        int chanceRemaining = 0;
        for (CaseItem caseItem : shuffledItems) {
            if (!RequestPlayerOwnedCases.hasCaseItem(player, caseItem.getName())) {
                // Skip if we are supposed to be getting a certain type
                if (caseToOpen.isLegendary()) {
                    if (!caseItem.getCaseItemRarity().isLegendary()) {
                        continue;
                    }
                } else if (caseToOpen.isSuperRare()) {
                    if (!caseItem.getCaseItemRarity().isSuperRare()) {
                        continue;
                    }
                } else if (caseToOpen.isRare()) {
                    if (!caseItem.getCaseItemRarity().isRare()) {
                        continue;
                    }
                }

                // Store the case item as many times as we need to
                int chance = items.get(caseItem);
                for (int i = 0; i < chance; i++) {
                    mapping.put(chanceRemaining + i, caseItem);
                }

                // Keep track of total chance
                chanceRemaining += chance;
            }
        }

        // Check if they can't get anymore rare/super rare/legendary items
        if (mapping.isEmpty()) {
            // If not, make it a normal case
            for (CaseItem caseItem : shuffledItems) {
                if (!RequestPlayerOwnedCases.hasCaseItem(player, caseItem.getName())) {
                    int chance = items.get(caseItem);
                    for (int i = 0; i < chance; i++) {
                        mapping.put(chanceRemaining + i, caseItem);
                    }

                    chanceRemaining += chance;
                }
            }
        }

        for (int i = 0; i < numberOfItems; i++) {
            // Generate a random number and pick their prize
            int n = Gberry.generateRandomInt(0, chanceRemaining);
            CaseItem caseItem = mapping.get(n);
            caseItemStacks.add(caseItem.getItemStack());
        }

        return caseItemStacks;
    }

    public static Collection<CaseItem> getCaseItems(CaseTier tier, Gberry.ServerType serverType) {
        return CaseManager.caseItems.get(serverType).get(tier).keySet();
    }

    public static class CaseHolder implements InventoryHolder {

        @Override
        public Inventory getInventory() {
            return null;
        }

    }

}
