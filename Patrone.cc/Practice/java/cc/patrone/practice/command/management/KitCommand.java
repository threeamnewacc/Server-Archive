package cc.patrone.practice.command.management;

import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.util.InventoryUtil;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class KitCommand {

    private PracticePlugin plugin;

    @Command(name = "kit", permission = "practice.command.kit", inGameOnly = true)
    public void kit(CommandArgs args) {
        args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <setKit>");
        return;
    }

    @Command(name = "kit.setkit", permission = "practice.command.kit", inGameOnly = true)
    public void kitSetKit(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <name>");
            return;
        }
        String kitName = args.getArgs(0);
        Kit kit = Kit.getKit(kitName);
        if (kit == null) {
            args.getSender().sendMessage(ChatColor.RED + "That kit doesn't exist.");
            return;
        }
        try {
            kit.setInventory(InventoryUtil.fromBase64(InventoryUtil.toBase64(args.getPlayer().getInventory())));
            kit.setArmor(InventoryUtil.itemStackArrayFromBase64(InventoryUtil.itemStackArrayToBase64(args.getPlayer().getInventory().getArmorContents())));
            args.getSender().sendMessage(ChatColor.GREEN + "You have set the kit for kit " + kit.getName() + ".");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
