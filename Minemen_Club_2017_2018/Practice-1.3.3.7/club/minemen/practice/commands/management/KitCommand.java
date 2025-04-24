// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.management;

import org.bukkit.inventory.ItemStack;
import java.util.Iterator;
import club.minemen.practice.arena.Arena;
import org.bukkit.GameMode;
import club.minemen.core.util.finalutil.ItemUtil;
import org.bukkit.Material;
import club.minemen.practice.kit.Kit;
import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.core.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;

public class KitCommand extends Command
{
    private static final String NO_KIT;
    private static final String NO_ARENA;
    private final Practice plugin;
    
    public KitCommand() {
        super("kit");
        this.plugin = Practice.getInstance();
        this.setDescription("Manage server kits.");
        this.setUsage(CC.RED + "Usage: /kit <subcommand> [args]");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        final Player player = (Player)sender;
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        final String lowerCase = args[0].toLowerCase();
        switch (lowerCase) {
            case "create": {
                if (kit == null) {
                    this.plugin.getKitManager().createKit(args[1]);
                    sender.sendMessage(CC.GREEN + "Successfully created kit " + args[1] + ".");
                    break;
                }
                sender.sendMessage(CC.RED + "That kit already exists!");
                break;
            }
            case "delete": {
                if (kit != null) {
                    this.plugin.getKitManager().deleteKit(args[1]);
                    sender.sendMessage(CC.GREEN + "Successfully deleted kit " + args[1] + ".");
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "disable":
            case "enable": {
                if (kit != null) {
                    kit.setEnabled(!kit.isEnabled());
                    sender.sendMessage(kit.isEnabled() ? (CC.GREEN + "Successfully enabled kit " + args[1] + ".") : (CC.RED + "Successfully disabled kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "combo": {
                if (kit != null) {
                    kit.setCombo(!kit.isCombo());
                    sender.sendMessage(kit.isCombo() ? (CC.GREEN + "Successfully enabled combo mode for kit " + args[1] + ".") : (CC.RED + "Successfully disabled combo mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "sumo": {
                if (kit != null) {
                    kit.setSumo(!kit.isSumo());
                    sender.sendMessage(kit.isCombo() ? (CC.GREEN + "Successfully enabled sumo mode for kit " + args[1] + ".") : (CC.RED + "Successfully disabled sumo mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "build": {
                if (kit != null) {
                    kit.setBuild(!kit.isBuild());
                    sender.sendMessage(kit.isBuild() ? (CC.GREEN + "Successfully enabled build mode for kit " + args[1] + ".") : (CC.RED + "Successfully disabled build mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "spleef": {
                if (kit != null) {
                    kit.setSpleef(!kit.isSpleef());
                    sender.sendMessage(kit.isSpleef() ? (CC.GREEN + "Successfully enabled spleef mode for kit " + args[1] + ".") : (CC.RED + "Successfully disabled spleef mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "ranked": {
                if (kit != null) {
                    kit.setRanked(!kit.isRanked());
                    sender.sendMessage(kit.isRanked() ? (CC.GREEN + "Successfully enabled ranked mode for kit " + args[1] + ".") : (CC.RED + "Successfully disabled ranked mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "excludearenafromallkitsbut": {
                if (args.length < 2) {
                    sender.sendMessage(this.usageMessage);
                    return true;
                }
                if (kit != null) {
                    final Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                    if (arena != null) {
                        for (final Kit loopKit : this.plugin.getKitManager().getKits()) {
                            if (!loopKit.equals(kit)) {
                                player.performCommand("kit excludearena " + loopKit.getName() + " " + arena.getName());
                            }
                        }
                    }
                    else {
                        sender.sendMessage(KitCommand.NO_ARENA);
                    }
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "excludearena": {
                if (args.length < 3) {
                    sender.sendMessage(this.usageMessage);
                    return true;
                }
                if (kit != null) {
                    final Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                    if (arena != null) {
                        kit.excludeArena(arena.getName());
                        sender.sendMessage(kit.getExcludedArenas().contains(arena.getName()) ? (CC.GREEN + "Arena " + arena.getName() + " is now excluded from kit " + args[1] + ".") : (CC.GREEN + "Arena " + arena.getName() + " is no longer excluded from kit " + args[1] + "."));
                    }
                    else {
                        sender.sendMessage(KitCommand.NO_ARENA);
                    }
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "whitelistarena": {
                if (args.length < 3) {
                    sender.sendMessage(this.usageMessage);
                    return true;
                }
                if (kit != null) {
                    final Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                    if (arena != null) {
                        kit.whitelistArena(arena.getName());
                        sender.sendMessage(kit.getArenaWhiteList().contains(arena.getName()) ? (CC.GREEN + "Arena " + arena.getName() + " is now whitelisted to kit " + args[1] + ".") : (CC.GREEN + "Arena " + arena.getName() + " is no longer whitelisted to kit " + args[1] + "."));
                    }
                    else {
                        sender.sendMessage(KitCommand.NO_ARENA);
                    }
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "icon": {
                if (kit == null) {
                    sender.sendMessage(KitCommand.NO_KIT);
                    break;
                }
                if (player.getItemInHand().getType() != Material.AIR) {
                    final ItemStack icon = ItemUtil.renameItem(player.getItemInHand().clone(), CC.GREEN + kit.getName());
                    kit.setIcon(icon);
                    sender.sendMessage(CC.GREEN + "Successfully set icon for kit " + args[1] + ".");
                    break;
                }
                player.sendMessage(CC.RED + "You must be holding an item to set the kit icon!");
                break;
            }
            case "setinv": {
                if (kit == null) {
                    sender.sendMessage(KitCommand.NO_KIT);
                    break;
                }
                if (player.getGameMode() == GameMode.CREATIVE) {
                    sender.sendMessage(CC.RED + "You can't set item contents in creative mode!");
                    break;
                }
                player.updateInventory();
                kit.setContents(player.getInventory().getContents());
                kit.setArmor(player.getInventory().getArmorContents());
                sender.sendMessage(CC.GREEN + "Successfully set kit contents for " + args[1] + ".");
                break;
            }
            case "getinv": {
                if (kit != null) {
                    player.getInventory().setContents(kit.getContents());
                    player.getInventory().setArmorContents(kit.getArmor());
                    player.updateInventory();
                    sender.sendMessage(CC.GREEN + "Successfully retrieved kit contents from " + args[1] + ".");
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            case "seteditinv": {
                if (kit == null) {
                    sender.sendMessage(KitCommand.NO_KIT);
                    break;
                }
                if (player.getGameMode() == GameMode.CREATIVE) {
                    sender.sendMessage(CC.RED + "You can't set item contents in creative mode!");
                    break;
                }
                player.updateInventory();
                kit.setKitEditContents(player.getInventory().getContents());
                sender.sendMessage(CC.GREEN + "Successfully set edit kit contents for " + args[1] + ".");
                break;
            }
            case "geteditinv": {
                if (kit != null) {
                    player.getInventory().setContents(kit.getKitEditContents());
                    player.updateInventory();
                    sender.sendMessage(CC.GREEN + "Successfully retrieved edit kit contents from " + args[1] + ".");
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            }
            default: {
                sender.sendMessage(this.usageMessage);
                break;
            }
        }
        return true;
    }
    
    static {
        NO_KIT = CC.RED + "That kit doesn't exist!";
        NO_ARENA = CC.RED + "That arena doesn't exist!";
    }
}
