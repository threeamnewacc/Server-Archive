package me.devkevin.practice.kit.command;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright 01/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class KitManageCommand extends PracticeCommand {

    private static final Practice plugin = Practice.getInstance();

    private final String NO_KIT = ChatColor.RED + "That kit doesn't exist!";
    private final String NO_ARENA = ChatColor.RED + "That arena doesn't exist!";

    @Command(name = "kit", inGameOnly = true, usage = "&cUsage: /kit <subcommand> [args]", permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        Player player = command.getPlayer();
        String[] args = command.getArgs();


        if (args[0].equalsIgnoreCase("save")) {
            plugin.getKitManager().reloadKits();
            player.sendMessage(ChatColor.GREEN + "Successfully saved and reloaded all kits.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&7Kit Commands Help: &f/kit <subCommand>"));
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&7 • &b/kit list"));
            player.sendMessage(CC.translate("&7 • &b/kit info <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit create <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit delete <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit whitelistArena <kitName> <arenaName>"));
            player.sendMessage(" ");
            player.sendMessage(CC.translate("&7 • &b/kit enable <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit disable <kitName>"));
            player.sendMessage(" ");
            player.sendMessage(CC.translate("&7 • &b/kit setinv <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit getinv <kitName>"));
            player.sendMessage(" ");
            player.sendMessage(CC.translate("&7 • &b/kit seteditinv <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit geteditinv <kitName>"));
            player.sendMessage(" ");
            player.sendMessage(CC.translate("&7 • &b/kit priority <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit refillinv <kitName>"));
            player.sendMessage(" ");
            player.sendMessage(CC.translate("&7 • &b/kit ranked <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit build <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit combo <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit sumo <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit skywars <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit bridges <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit spleef <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit boxing <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit bedwars <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit mlgrush <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit battlerush <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit stickfight <kitName>"));
            player.sendMessage(CC.translate("&7 • &b/kit parkour <kitName>"));

            player.sendMessage(CC.CHAT_BAR);
            return;
        }

        Kit kit = plugin.getKitManager().getKit(args[1]);

        switch (args[0].toLowerCase()) {
            case "create":
                if (kit == null) {
                    plugin.getKitManager().createKit(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Successfully created kit " + args[1] + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "That kit already exists!");
                }
                break;
            case "delete":
                if (kit != null) {
                    plugin.getKitManager().deleteKit(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Successfully deleted kit " + args[1] + ".");
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            case "disable":
            case "enable":
                if (kit != null) {
                    kit.setEnabled(!kit.isEnabled());
                    sender.sendMessage(kit.isEnabled() ? ChatColor.GREEN + "Successfully enabled kit " + args[1] + "." :
                            ChatColor.RED + "Successfully disabled kit " + args[1] + ".");
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            case "combo":
                if (kit != null) {
                    kit.setCombo(!kit.isCombo());
                    sender.sendMessage(
                            kit.isCombo() ? ChatColor.GREEN + "Successfully enabled combo mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled combo mode for kit " + args[1] + ".");
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            case "sumo":
                if (kit != null) {
                    kit.setSumo(!kit.isSumo());
                    sender.sendMessage(
                            kit.isSumo() ? ChatColor.GREEN + "Successfully enabled sumo mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled sumo mode for kit " + args[1] + ".");
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            case "build":
                if (kit != null) {
                    kit.setBuild(!kit.isBuild());
                    sender.sendMessage(
                            kit.isBuild() ? ChatColor.GREEN + "Successfully enabled build mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled build mode for kit " + args[1] + ".");
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            case "spleef":
                if (kit != null) {
                    kit.setSpleef(!kit.isSpleef());
                    sender.sendMessage(
                            kit.isSpleef() ? ChatColor.GREEN + "Successfully enabled spleef mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled spleef mode for kit " + args[1] + ".");
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            case "ranked":
                if (kit != null) {
                    kit.setRanked(!kit.isRanked());
                    sender.sendMessage(
                            kit.isRanked() ? ChatColor.GREEN + "Successfully enabled ranked mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled ranked mode for kit " + args[1] + ".");
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            case "boxing":
                if (kit != null) {
                    kit.setBoxing(!kit.isBoxing());
                    sender.sendMessage(kit.isBoxing()
                                    ? ChatColor.GREEN + "Successfully enabled boxing mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled boxing mode for kit " + args[1] + "."
                    );
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            case "whitelistarena":
                if (args.length < 3) {
                    sender.sendMessage(command.getCommand().getUsage());
                    return;
                }
                if (kit != null) {
                    Arena arena = plugin.getArenaManager().getArena(args[2]);

                    if (arena != null) {
                        kit.whitelistArena(arena.getName());
                        sender.sendMessage(kit.getArenaWhiteList().contains(arena.getName()) ?
                                ChatColor.GREEN + "Arena " + arena.getName() + " is now whitelisted to kit " + args[1] + "."
                                : ChatColor.GREEN + "Arena " + arena.getName() + " is no longer whitelisted to kit " + args[1] + ".");
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
            case "refillinv":
                if (kit != null) {
                    player.getInventory().setContents(kit.getKitEditContents());
                    sender.sendMessage(CC.GREEN + "Successfully retrieved editable kit inventory from " + kit.getName() + ".");
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            default:
                sender.sendMessage(command.getCommand().getUsage());
                break;
        }
    }
}
