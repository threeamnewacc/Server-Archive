package us.zonix.practice.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;
import me.maiko.dexter.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.util.ItemBuilder;
import us.zonix.practice.util.inventory.InventoryUI;

public class LeaderboardCommand extends Command {
    private final Practice plugin = Practice.getInstance();
    private final InventoryUI leaderboardInventory = new InventoryUI(ChatColor.DARK_GRAY + "Leaderboards", true, 2);

    public LeaderboardCommand() {
        super("leaderboard");
        this.setAliases(Arrays.asList("lb", "leaderboards"));
        this.setUsage(ChatColor.RED + "Usage: /leaderboard");
        (new BukkitRunnable() {
            public void run() {
                LeaderboardCommand.this.updateInventory();
            }
        }).runTaskTimerAsynchronously(this.plugin, 0L, 1200L);
    }

    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            player.openInventory(this.leaderboardInventory.getCurrentPage());
            return true;
        }
    }

    private void updateInventory() {
        for (Kit kit : this.plugin.getKitManager().getKits()) {
            if (kit.isEnabled() && kit.isRanked() && kit.isPremium()) {
                ItemBuilder kitItemBuilder = new ItemBuilder(kit.getIcon().getType())
                    .durability(kit.getIcon().getDurability())
                    .name(ChatColor.RED + kit.getName());
                HashMap<String, Integer> eloMap = this.plugin.getPlayerManager().findTopEloByKit(kit.getName(), 10);
                int position = 1;

                for (Entry<String, Integer> entry : eloMap.entrySet()) {
                    String username = Profile.getNameByUUID(UUID.fromString(entry.getKey()));
                    if (username != null) {
                        switch (position) {
                            case 1:
                                kitItemBuilder.lore("&a1) &f" + username + " &7(" + entry.getValue() + " ELO)");
                                break;
                            case 2:
                                kitItemBuilder.lore("&f2) &f" + username + " &7(" + entry.getValue() + " ELO)");
                                break;
                            case 3:
                                kitItemBuilder.lore("&63) &f" + username + " &7(" + entry.getValue() + " ELO)");
                                break;
                            default:
                                kitItemBuilder.lore("&7" + position + ") &f" + username + " &7(" + entry.getValue() + " ELO)");
                        }

                        position++;
                    }
                }

                this.leaderboardInventory.setItem(kit.getPriority(), new InventoryUI.EmptyClickableItem(kitItemBuilder.build()));
            }
        }
    }
}
