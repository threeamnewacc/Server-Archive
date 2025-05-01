package net.badlion.uhc.commands;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.uhc.commands.handlers.GameModeHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ScenariosCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (GameModeHandler.GAME_MODES.size() == 0) {
            sender.sendMessage(ChatColor.RED + "This game does not have any special scenarios.");
            return true;
        }

        if (sender instanceof Player) {
            final Player player = (Player) sender;

            new ScenariosInventory().openInventory(player);
        }

        return true;
    }

    public class ScenariosInventory {

        private SmellyInventory smellyInventory;

        public ScenariosInventory() {
            SmellyInventory smellyInventory = new SmellyInventory(new ScenarioScreenHandler(), 18,
                                                                         ChatColor.AQUA + ChatColor.BOLD.toString() + "Scenarios for this game");

            for (String gamemode : GameModeHandler.GAME_MODES) {
                smellyInventory.getMainInventory().addItem(GameModeHandler.gamemodes.get(gamemode).getExplanationItem());
            }

            ItemStack cancelReportItem = new ItemStack(Material.WOOL, 1, (short) 14);
            ItemMeta cancelInventoryItemMeta = cancelReportItem.getItemMeta();
            cancelInventoryItemMeta.setDisplayName(ChatColor.GREEN + "Close");
            cancelReportItem.setItemMeta(cancelInventoryItemMeta); // 'cancelReportItem' good copy pasting mate

            smellyInventory.getMainInventory().setItem(smellyInventory.getMainInventory().getSize() - 1, cancelReportItem);

            this.smellyInventory = smellyInventory;
        }

        public void openInventory(final Player player) {
            if (player.getOpenInventory() != null) {
                BukkitUtil.runTaskNextTick(new Runnable() {
                    @Override
                    public void run() {
                        if (player.isOnline()) {
                            player.closeInventory();
                            player.openInventory(ScenariosInventory.this.smellyInventory.getMainInventory());
                        }
                    }
                });
            } else {
                player.openInventory(this.smellyInventory.getMainInventory());
            }
        }

        public class ScenarioScreenHandler implements SmellyInventory.SmellyInventoryHandler {

            @Override
            public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
                if (slot == fakeHolder.getInventory().getSize() - 1) {
                    player.closeInventory();
                }
            }

            @Override
            public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

            }

        }
    }


}
