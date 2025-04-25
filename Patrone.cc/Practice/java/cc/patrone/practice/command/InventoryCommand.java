package cc.patrone.practice.command;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.util.InventorySnapshot;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class InventoryCommand {

    private PracticePlugin plugin;

    @Command(name = "inventory", aliases = {"inv"}, inGameOnly = true)
    public void inventory(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = this.plugin.getServer().getPlayer(args.getArgs(0));
        if (target == null || args.getArgs(0).equalsIgnoreCase(CorePlugin.verzideName)) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        if (InventorySnapshot.getByPlayer(target) == null) {
            args.getSender().sendMessage(ChatColor.RED + "No inventory was found for that player.");
            return;
        }
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
        args.getPlayer().openInventory(InventorySnapshot.getByPlayer(target).getInventory());
        practiceProfile.setOpenInventory(InventorySnapshot.getByPlayer(target));
        return;
    }
}
