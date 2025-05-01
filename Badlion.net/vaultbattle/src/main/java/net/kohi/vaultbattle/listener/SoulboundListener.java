package net.kohi.vaultbattle.listener;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.utils.ItemStackUtil;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.util.Soulbound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class SoulboundListener implements Listener {

    private final VaultBattlePlugin plugin;

    List<InventoryAction> actions = Arrays.asList(InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.SWAP_WITH_CURSOR);

    public SoulboundListener(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (Soulbound.isSoulbound(event.getItemDrop().getItemStack())) {
            event.getPlayer().sendFormattedMessage("{0}Soulbound destroyed", ChatColor.RED, ItemStackUtil.getName(event.getItemDrop().getItemStack()));
            event.getPlayer().playSound(event.getPlayer().getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.0F, 1.0F);
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(Soulbound::isSoulbound);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        handleInventoryEvent(event);
    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent event) {
        handleInventoryEvent(event);
    }

    private void handleInventoryEvent(InventoryInteractEvent event) {
        Inventory topInv = event.getView().getTopInventory();
        if (topInv == null || topInv.getType() == InventoryType.CRAFTING || topInv.getType() == InventoryType.WORKBENCH) {
            // no storage inv open, just break out
            return;
        }
        Player player = (Player) event.getWhoClicked();
        // delayed check the top inv for soulbound items and destroy them
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (int i = 0; i < topInv.getSize(); i++) {
                if (topInv.getItem(i) != null && Soulbound.isSoulbound(topInv.getItem(i))) {
                    player.sendFormattedMessage("{0}Soulbound destroyed", ChatColor.RED, ItemStackUtil.getName(topInv.getItem(i)));
                    player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.0F, 1.0F);
                    topInv.setItem(i, null);
                }
            }
        });
    }
}
