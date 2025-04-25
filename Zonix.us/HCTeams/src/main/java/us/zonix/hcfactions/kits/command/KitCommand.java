package us.zonix.hcfactions.kits.command;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.kits.Kit;
import us.zonix.hcfactions.kits.command.subcommand.*;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.DateUtil;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.kits.Kit;
import us.zonix.hcfactions.kits.command.subcommand.KitCreateCommand;
import us.zonix.hcfactions.kits.command.subcommand.KitDeleteCommand;
import us.zonix.hcfactions.kits.command.subcommand.KitListCommand;
import us.zonix.hcfactions.kits.command.subcommand.KitUpdateCommand;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.DateUtil;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

import java.text.DecimalFormat;
import java.util.Arrays;

public class KitCommand extends PluginCommand {

    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");

    public KitCommand() {
        new KitCreateCommand();
        new KitDeleteCommand();
        new KitListCommand();
        new KitUpdateCommand();
    }

    @Command(name = "kit", aliases = "kits", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if(FactionsPlugin.getInstance().isKitmapMode()) {
            player.sendMessage(ChatColor.RED + "This command is not available for KitMap.");
            return;
        }

        if(args.length == 0) {
            player.performCommand("gkits");
        }
    }

    public static void openKitsInventory(Player player) {

        Inventory inventory = Bukkit.createInventory(player, 27, "HCF Kits");

        ItemStack spacer = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name(" ").build();

        int[] slots = new int[] {0,1,2,3,4,5,6,7,8,9,11,13,15,17,18,19,20,21,22,23,24,25,26};

        for(int i : slots) {
            inventory.setItem(i, spacer);
        }

        int count = 1;

        for (Kit kit : Kit.getKits()) {
            String cooldown = Profile.getByPlayer(player) != null && isCooldownActive(Profile.getByPlayer(player), kit) ? ChatColor.DARK_GREEN + "Cooldown: " + ChatColor.GRAY + getTimeLeft(Profile.getByPlayer(player), kit) : ChatColor.DARK_GREEN + "Cooldown: " + ChatColor.GRAY + "None";
            inventory.setItem(inventory.firstEmpty(), new ItemBuilder(Material.STAINED_GLASS_PANE).enchantment(Enchantment.DURABILITY).durability(count).name(ChatColor.RED + "Kit: " + ChatColor.GRAY + kit.getName()).lore(cooldown).lore(ChatColor.GRAY + ChatColor.BOLD.toString() + "* " + ChatColor.GOLD + "Right Click to preview this Kit").lore(ChatColor.GRAY + ChatColor.BOLD.toString() + "* " + ChatColor.GOLD + "Left Click to use this Kit").build());
            count++;
        }

        player.openInventory(inventory);
    }

    public static void openPreviewInventory(Player player, Kit kit) {
        Inventory inventory = Bukkit.createInventory(player, 9 * 6, kit.getName() + " Preview");

        int count = 0;

        for(ItemStack itemStack : kit.getItems()) {
            inventory.setItem(count, itemStack);
            count++;
        }

        inventory.setItem(53, new ItemBuilder(Material.ARROW).name(ChatColor.RED + "⏎ Go Back").build());

        player.openInventory(inventory);
    }

    public static String getTimeLeft(Profile profile, Kit kit) {

        if (!profile.getKitDelay().containsKey(kit)) {
            return "None";
        }

        long delay = profile.getKitDelay().get(kit);

        return DateUtil.convertTime((delay - System.currentTimeMillis()) / 1000L);
    }

    public static boolean isCooldownActive(Profile profile, Kit kit) {

        if(!profile.getKitDelay().containsKey(kit)) {
            return false;
        }

        long delay = profile.getKitDelay().get(kit);

        return delay > System.currentTimeMillis();
    }
}
