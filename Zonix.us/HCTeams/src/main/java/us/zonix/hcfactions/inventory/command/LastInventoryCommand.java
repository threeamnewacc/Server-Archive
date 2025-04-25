package us.zonix.hcfactions.inventory.command;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.fight.ProfileFight;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.InventorySerialisation;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.fight.ProfileFight;
import us.zonix.hcfactions.util.command.CommandArgs;

import java.io.IOException;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class LastInventoryCommand extends PluginCommand {

    @Command(name = "lastinventory", aliases = {"lastinv", "restoreinv", "restoreinventory"}, permission = Rank.ADMINISTRATOR)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        Profile toRestore;
        Player toRestorePlayer = null;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/" + command.getLabel() + " <player>");
            return;
        } else {

             toRestorePlayer = Bukkit.getPlayer(args[0]);

            if (toRestorePlayer != null) {
                toRestore = Profile.getByPlayer(toRestorePlayer);
            } else {
                toRestore = Profile.getByName(args[0]);
            }
        }

        if (toRestore == null) {
            player.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found.");
            return;
        }

        UUID uuid = null;
        if (args.length > 1) {
            try {
                uuid = UUID.fromString(args[1]);
            } catch (Exception ex) {
                player.sendMessage(ChatColor.RED + "Invalid query.");
                return;
            }
        }

        ItemStack[] contents = null, armor = null;
        if (!toRestore.getFights().isEmpty()) {
            for (ProfileFight oneFight : toRestore.getFights()) {
                if (oneFight.getKiller() != null && oneFight.getKiller().getName() != null && oneFight.getKiller().getName().equals(toRestore.getName())) continue;
                if (uuid != null && !oneFight.getUuid().equals(uuid)) continue;
                contents = oneFight.getContents();
                armor = oneFight.getArmor();
            }
        } else {
            Document fightDocument;
            FindIterable iterable = main.getFactionsDatabase().getFights().find(eq("killed", toRestore.getUuid().toString())).sort(descending("occurred_at"));
            if (uuid == null) {
                fightDocument = (Document) iterable.first();
            } else {
                fightDocument = (Document) iterable.filter(eq("uuid", uuid.toString())).first();
            }

            if (fightDocument == null) {
                player.sendMessage(ChatColor.RED + toRestore.getName() + " has no previous inventories to restore. (document)");
                return;
            }

            try {
                contents = InventorySerialisation.itemStackArrayFromJson(fightDocument.getString("contents"));
                armor = InventorySerialisation.itemStackArrayFromJson(fightDocument.getString("armor"));
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "An error occurred when attempting to grab that inventory.");
                return;
            }
        }

        if (contents == null || armor == null) {
            player.sendMessage(ChatColor.RED + "An error occurred when attempting to grab that inventory.");
            return;
        }

        if(toRestorePlayer != null && toRestorePlayer.isOnline()) {
            toRestorePlayer.getInventory().setContents(contents);
            toRestorePlayer.getInventory().setArmorContents(armor);
            toRestorePlayer.sendMessage(ChatColor.RED + "Inventory successfully received.");
            toRestorePlayer.sendMessage(ChatColor.RED + "Inventory successfully sent.");
        }

    }
}
