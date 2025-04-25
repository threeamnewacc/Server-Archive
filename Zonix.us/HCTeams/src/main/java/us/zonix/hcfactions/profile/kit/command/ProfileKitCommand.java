package us.zonix.hcfactions.profile.kit.command;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.kit.ProfileKitEnergy;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.profile.kit.ProfileKit;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.kit.ProfileKit;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

import java.util.Random;

public class ProfileKitCommand extends PluginCommand {
    @Command(name = "pvp.kit", aliases = {"pvp.class", "class"}, permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/" + command.getLabel().replace(".", " ") + " <class> <target>");
            return;
        }

        ProfileKit kit;
        try {
            kit = ProfileKit.valueOf(args[0].toUpperCase());
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Invalid class kit.");
            return;
        }

        Profile profile;
        Player player;
        if (args.length == 1) {
            if (sender instanceof Player) {
                player = (Player) sender;
                profile = Profile.getByPlayer(player);
            } else {
                sender.sendMessage(org.bukkit.ChatColor.RED + "You're console.");
                return;
            }
        } else {
            player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                profile = Profile.getByPlayer(player);
            } else {
                profile = null;
            }
        }

        if (profile == null) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "No player with name '" + args[1] + "' found.");
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{kit.getEnchantedArmor()[0], kit.getEnchantedArmor()[1], kit.getEnchantedArmor()[2], kit.getEnchantedArmor()[3]});
        player.getInventory().addItem(kit.getEnchantedArmor()[kit.getEnchantedArmor().length - 1]);
        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 16));

        Material food;
        int random = new Random().nextInt(3);
        if (random == 1) {
            food = Material.GOLDEN_CARROT;
        } else if (random == 2) {
            food = Material.BAKED_POTATO;
        } else {
            food = Material.COOKED_BEEF;
        }

        player.getInventory().addItem(new ItemStack(food, 64));
        for (int i : new int[]{8, 35, 26, 17, 16, 15, 14}) {
            player.getInventory().setItem(i, new ItemBuilder(Material.POTION).durability(8226).build());
        }

        while (player.getInventory().firstEmpty() != -1) {
            player.getInventory().setItem(player.getInventory().firstEmpty(), new ItemBuilder(Material.POTION).durability(16421).build());
        }

        if (profile.getKitWarmup() != null) {
            profile.setKitWarmup(null);
        }

        if (profile.getEnergy() != null) {
            profile.setEnergy(null);
        }

        if (kit != ProfileKit.DIAMOND) {
            profile.setKit(kit);
            if (kit == ProfileKit.BARD) {
                profile.setEnergy(new ProfileKitEnergy());
            }
        }

        sender.sendMessage(ChatColor.RED + "Successfully gave " + kit.name().toLowerCase() + " class to " + player.getName() + ".");

    }
}
