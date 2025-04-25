package us.zonix.practice.commands.management;

import java.util.Optional;
import net.edater.spigot.EdaterSpigot;
import net.edater.spigot.knockback.KnockbackProfile;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.util.ItemUtil;

public class KitCommand extends Command {
    private static final String NO_KIT = ChatColor.RED + "That kit doesn't exist!";
    private static final String NO_ARENA = ChatColor.RED + "That arena doesn't exist!";
    private final Practice plugin = Practice.getInstance();

    public KitCommand() {
        super("kit");
        this.setDescription("Kit command.");
        this.setUsage(ChatColor.RED + "Usage: /kit <subcommand> [args]");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (!player.hasPermission("practice.admin.kits")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            } else if (args.length < 2) {
                sender.sendMessage(this.usageMessage);
                return true;
            } else {
                Kit kit = this.plugin.getKitManager().getKit(args[1]);
                String var6 = args[0].toLowerCase();
                switch (var6) {
                    case "create":
                        if (kit == null) {
                            this.plugin.getKitManager().createKit(args[1]);
                            sender.sendMessage(ChatColor.GREEN + "Successfully created kit " + args[1] + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "That kit already exists!");
                        }
                        break;
                    case "delete":
                        if (kit != null) {
                            this.plugin.getKitManager().deleteKit(args[1]);
                            sender.sendMessage(ChatColor.GREEN + "Successfully deleted kit " + args[1] + ".");
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "disable":
                    case "enable":
                        if (kit != null) {
                            kit.setEnabled(!kit.isEnabled());
                            sender.sendMessage(
                                kit.isEnabled()
                                    ? ChatColor.GREEN + "Successfully enabled kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled kit " + args[1] + "."
                            );
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "combo":
                        if (kit != null) {
                            kit.setCombo(!kit.isCombo());
                            sender.sendMessage(
                                kit.isCombo()
                                    ? ChatColor.GREEN + "Successfully enabled combo mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled combo mode for kit " + args[1] + "."
                            );
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "sumo":
                        if (kit != null) {
                            kit.setSumo(!kit.isSumo());
                            sender.sendMessage(
                                kit.isSumo()
                                    ? ChatColor.GREEN + "Successfully enabled sumo mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled sumo mode for kit " + args[1] + "."
                            );
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "build":
                        if (kit != null) {
                            kit.setBuild(!kit.isBuild());
                            sender.sendMessage(
                                kit.isBuild()
                                    ? ChatColor.GREEN + "Successfully enabled build mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled build mode for kit " + args[1] + "."
                            );
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "spleef":
                        if (kit != null) {
                            kit.setSpleef(!kit.isSpleef());
                            sender.sendMessage(
                                kit.isSpleef()
                                    ? ChatColor.GREEN + "Successfully enabled spleef mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled spleef mode for kit " + args[1] + "."
                            );
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "parkour":
                        if (kit != null) {
                            kit.setParkour(!kit.isParkour());
                            sender.sendMessage(
                                kit.isParkour()
                                    ? ChatColor.GREEN + "Successfully enabled parkour mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled parkour mode for kit " + args[1] + "."
                            );
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "bestofthree":
                        if (kit != null) {
                            kit.setBestOfThree(!kit.isBestOfThree());
                            sender.sendMessage(
                                kit.isBestOfThree()
                                    ? ChatColor.GREEN + "Successfully enabled best of three mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled best of three mode for kit " + args[1] + "."
                            );
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "ranked":
                        if (kit != null) {
                            kit.setRanked(!kit.isRanked());
                            sender.sendMessage(
                                kit.isRanked()
                                    ? ChatColor.GREEN + "Successfully enabled ranked mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled ranked mode for kit " + args[1] + "."
                            );
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "premium":
                        if (kit != null) {
                            kit.setPremium(!kit.isPremium());
                            sender.sendMessage(
                                kit.isPremium()
                                    ? ChatColor.GREEN + "Successfully enabled premium mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled premium mode for kit " + args[1] + "."
                            );
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "excludeall":
                        if (args.length < 2) {
                            sender.sendMessage(this.usageMessage);
                            return true;
                        }

                        if (kit != null) {
                            Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                            if (arena != null) {
                                for (Kit loopKit : this.plugin.getKitManager().getKits()) {
                                    if (!loopKit.equals(kit)) {
                                        player.performCommand("kit excludearena " + loopKit.getName() + " " + arena.getName());
                                    }
                                }
                            } else {
                                sender.sendMessage(NO_ARENA);
                            }
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "excludearena":
                        if (args.length < 3) {
                            sender.sendMessage(this.usageMessage);
                            return true;
                        }

                        if (kit != null) {
                            Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                            if (arena != null) {
                                kit.excludeArena(arena.getName());
                                sender.sendMessage(
                                    kit.getExcludedArenas().contains(arena.getName())
                                        ? ChatColor.GREEN + "Arena " + arena.getName() + " is now excluded from kit " + args[1] + "."
                                        : ChatColor.GREEN + "Arena " + arena.getName() + " is no longer excluded from kit " + args[1] + "."
                                );
                            } else {
                                sender.sendMessage(NO_ARENA);
                            }
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "whitelistarena":
                        if (args.length < 3) {
                            sender.sendMessage(this.usageMessage);
                            return true;
                        }

                        if (kit != null) {
                            Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                            if (arena != null) {
                                kit.whitelistArena(arena.getName());
                                sender.sendMessage(
                                    kit.getArenaWhiteList().contains(arena.getName())
                                        ? ChatColor.GREEN + "Arena " + arena.getName() + " is now whitelisted to kit " + args[1] + "."
                                        : ChatColor.GREEN + "Arena " + arena.getName() + " is no longer whitelisted to kit " + args[1] + "."
                                );
                            } else {
                                sender.sendMessage(NO_ARENA);
                            }
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "icon":
                        if (kit != null) {
                            if (player.getItemInHand().getType() != Material.AIR) {
                                ItemStack icon = ItemUtil.renameItem(player.getItemInHand().clone(), ChatColor.GREEN + kit.getName());
                                kit.setIcon(icon);
                                sender.sendMessage(ChatColor.GREEN + "Successfully set icon for kit " + args[1] + ".");
                            } else {
                                player.sendMessage(ChatColor.RED + "You must be holding an item to set the kit icon!");
                            }
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "setinv":
                        if (kit != null) {
                            if (player.getGameMode() == GameMode.CREATIVE) {
                                sender.sendMessage(ChatColor.RED + "You can't set item contents in creative mode!");
                            } else {
                                player.updateInventory();
                                kit.setContents(player.getInventory().getContents());
                                kit.setArmor(player.getInventory().getArmorContents());
                                sender.sendMessage(ChatColor.GREEN + "Successfully set kit contents for " + args[1] + ".");
                            }
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "getinv":
                        if (kit != null) {
                            player.getInventory().setContents(kit.getContents());
                            player.getInventory().setArmorContents(kit.getArmor());
                            player.updateInventory();
                            sender.sendMessage(ChatColor.GREEN + "Successfully retrieved kit contents from " + args[1] + ".");
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "seteditinv":
                        if (kit != null) {
                            if (player.getGameMode() == GameMode.CREATIVE) {
                                sender.sendMessage(ChatColor.RED + "You can't set item contents in creative mode!");
                            } else {
                                player.updateInventory();
                                kit.setKitEditContents(player.getInventory().getContents());
                                sender.sendMessage(ChatColor.GREEN + "Successfully set edit kit contents for " + args[1] + ".");
                            }
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "geteditinv":
                        if (kit != null) {
                            player.getInventory().setContents(kit.getKitEditContents());
                            player.updateInventory();
                            sender.sendMessage(ChatColor.GREEN + "Successfully retrieved edit kit contents from " + args[1] + ".");
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "priority":
                        if (args.length < 3) {
                            sender.sendMessage(this.usageMessage);
                            return true;
                        }

                        if (kit != null) {
                            if (!NumberUtils.isNumber(args[2])) {
                                sender.sendMessage(this.usageMessage);
                                return true;
                            }

                            int priority = Integer.parseInt(args[2]);
                            kit.setPriority(priority);
                            sender.sendMessage(ChatColor.GREEN + "Successfully set priority to " + priority + ".");
                        } else {
                            sender.sendMessage(NO_KIT);
                        }
                        break;
                    case "setprofile":
                        if (args.length < 3) {
                            sender.sendMessage(this.usageMessage);
                            return true;
                        }

                        if (kit == null) {
                            sender.sendMessage(NO_KIT);
                            return true;
                        }

                        Optional<KnockbackProfile> knockbackProfile = EdaterSpigot.INSTANCE.getKnockbackHandler().getProfileByName(args[2]);
                        if (!knockbackProfile.isPresent()) {
                            sender.sendMessage(ChatColor.RED + "Knockback Profile not found.");
                            return true;
                        }

                        kit.setKnockbackProfile(knockbackProfile.get());
                        sender.sendMessage(ChatColor.GREEN + "Successfully set Knockback Profile to " + knockbackProfile.get().getName() + ".");
                        break;
                    default:
                        sender.sendMessage(this.usageMessage);
                }

                return true;
            }
        }
    }
}
