package net.badlion.arenasetup.inventory;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenasetup.ArenaSetup;
import net.badlion.arenasetup.SetupSession;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ArenaTypeInventory {

    private static final List<KitRuleSet> kitRuleSetList = KitRuleSet.getAllKitRuleSets();

    public static void openArenaTypeInventory(Player sender) {
        // Figure out how big our inventory needs to be

        int inventorySize = 36;

        String name = "Select the Arena Types";

        if (name.length() > 32) name = name.substring(0, 32);

        // Create smelly inventory
        SetupSession setupSession = ArenaSetup.getInstance().getSetupSessionMap().get(sender.getUniqueId());
        if (setupSession == null) {
            return;
        }
        SmellyInventory smellyInventory = new SmellyInventory(new ArenaTypeScreenHandler(setupSession), inventorySize, name);

        // Fill items
        ArenaTypeInventory.fillInventory(sender, smellyInventory.getMainInventory(), setupSession);

        // Open Inventory
        BukkitUtil.openInventory(sender, smellyInventory.getMainInventory());
    }

    private static void fillInventory(Player player, Inventory inventory, SetupSession setupSession) {
        inventory.clear();
        for (KitRuleSet kitRuleSet : ArenaTypeInventory.kitRuleSetList) {
            if (setupSession.getTypes().contains(kitRuleSet)) {
                ItemStack item = ItemStackUtil.createItem(Material.INK_SACK, 1, (short) 14, ChatColor.RED + "Remove " + kitRuleSet.getName(), (String[]) (new String[]{null, null}));
                inventory.setItem(kitRuleSet.getId(), item);
            } else {
                inventory.setItem(kitRuleSet.getId(), kitRuleSet.getKitItem());
            }
        }
    }

    private static class ArenaTypeScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        private final SetupSession setupSession;

        public ArenaTypeScreenHandler(SetupSession setupSession) {
            this.setupSession = setupSession;
        }

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            int rawSlot = event.getRawSlot();
            for (KitRuleSet kitRuleSet : ArenaTypeInventory.kitRuleSetList) {
                if (kitRuleSet.getId() == rawSlot) {
                    if(setupSession.getTypes().contains(kitRuleSet)){
                        setupSession.getTypes().remove(kitRuleSet);
                    }else {
                        setupSession.getTypes().add(kitRuleSet);
                    }
                    fillInventory(player, fakeHolder.getSmellyInventory().getMainInventory(), setupSession);
                    return;
                }
            }
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
        }

    }

}
