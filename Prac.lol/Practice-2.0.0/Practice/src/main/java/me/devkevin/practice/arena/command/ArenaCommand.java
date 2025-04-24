package me.devkevin.practice.arena.command;

import club.inverted.chatcolor.CC;
import com.google.common.collect.Lists;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.generate.ArenaCommandRunnable;
import me.devkevin.practice.arena.generate.ArenaCopyRemovalRunnable;
import me.devkevin.practice.arena.menu.ArenaManagerMenu;
import me.devkevin.practice.arena.standalone.StandaloneArena;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

;

/**
 * Copyright 31/12/2021 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ArenaCommand extends PracticeCommand {

    private final String NO_ARENA = ChatColor.RED + "That arena doesn't exist!";
    private final Practice plugin = Practice.getInstance();

    @Override @Command(name = "arena", permission = Rank.MANAGER, inGameOnly = true, description = "Arenas command.", usage = "&cUsage: /arena <subcommand> [args]")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {

            List<String> arenamsg = Lists.newArrayList();

            arenamsg.add(CC.CHAT_BAR);
            arenamsg.add("&4&oArena Setup Help");
            arenamsg.add(CC.CHAT_BAR);
            arenamsg.add("&c/arena create <arena> &7- &fCreates arena");
            arenamsg.add("&c/arena delete <arena> &7- &fDeletes arena");
            arenamsg.add("&c/arena enable <arena> &7- &fEnables arena");
            arenamsg.add("&c/arena disable <arena> &7- &fDisables arena");
            arenamsg.add("&c/arena info <arena> &7- &fShows arena information");
            arenamsg.add("&c/arena icon <arena> &7- &fSet the arena icon");
            arenamsg.add("&c/arena a <arena> &7- &f1st Player Spawn (Team Blue) (Team ID: 1)");
            arenamsg.add("&c/arena b <arena> &7- &f2nd Player Spawn (Team Red) (Team ID: 0)");
            arenamsg.add("&c/arena min <arena> &7- &fLowest corner pos of arena");
            arenamsg.add("&c/arena max <arena> &7- &fHighest corner pos of arena");
            arenamsg.add("&c/arena buildMax <arena> &7- &fHighest build height of arena");
            arenamsg.add("&c/arena deadZone <arena> &7- &fLowest player alive pos of arena");
            arenamsg.add("&c/arena portalprot <arena> <number> &7- &fPortal protection radius of arena");
            arenamsg.add(" ");
            arenamsg.add("&c/arena list &7- &fShows all arenas");
            arenamsg.add("&c/arena save &7- &fSaves all arenas");
            arenamsg.add("&c/arena manage &7- &fOpens arena management menu");
            arenamsg.add("&c/arena generate <arena> <copies> &7- &fCreates copies");
            arenamsg.add(CC.CHAT_BAR);

            for (String message : arenamsg) {
                player.sendMessage(CC.translate(message));
            }
            return;
        }

        if (args[0].equalsIgnoreCase("save")) {
            this.plugin.getArenaManager().reloadArenas();
            player.sendMessage(ChatColor.GREEN + "Successfully saved and reloaded all arenas.");
            return;
        }

        if (args[0].equalsIgnoreCase("manage")) {
            if (this.plugin.getArenaManager().getArenas().size() == 0) {
                player.sendMessage(CC.translate("&cThere are no arenas."));
                return;
            }

            new ArenaManagerMenu().openMenu(player);
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&4&lArenas List &7(&f" + plugin.getArenaManager().getArenas().size() + "&c in total&7)"));
            player.sendMessage(CC.CHAT_BAR);
            for (Arena arena : plugin.getArenaManager().getArenas().values()) {
                player.sendMessage(CC.translate(" &9&l▸ &c" + arena.getName() + " &8[" + (arena.isEnabled() ? "&aEnabled" : "&cDisabled") + "&8]"));
            }
            player.sendMessage(CC.CHAT_BAR);

            return;
        }

        if (args.length < 2) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        Arena arena = this.plugin.getArenaManager().getArena(args[1]);
        switch (args[0].toLowerCase()) {
            case "create":
                if (arena == null) {
                    this.plugin.getArenaManager().createArena(args[1]);
                    player.sendMessage(ChatColor.GREEN + "Successfully created arena " + args[1] + ".");
                } else {
                    player.sendMessage(ChatColor.RED + "That arena already exists!");
                }
                break;
            case "delete":
                if (arena != null) {
                    if (this.plugin.getArenaManager().getArena(args[1]).getStandaloneArenas().size() >= 1) {
                        int i = 0;
                        for (StandaloneArena copy : this.plugin.getArenaManager().getArena(args[1]).getStandaloneArenas()) {
                            new ArenaCopyRemovalRunnable(i, arena, copy).runTask(this.plugin);
                            i++;
                        }
                    }

                    this.plugin.getArenaManager().deleteArena(args[1]);
                    player.sendMessage(ChatColor.GREEN + "Successfully deleted arena " + args[1] + ".");
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "icon":
                if (arena != null) {
                    if (player.getItemInHand().getType() != Material.AIR) {
                        String icon = player.getItemInHand().getType().name();
                        int iconData = player.getItemInHand().getDurability();
                        arena.setIcon(icon);
                        arena.setIconData(iconData);
                        player.sendMessage(ChatColor.GREEN + "Successfully set icon for arena " + args[1] + ".");
                    } else {
                        player.sendMessage(ChatColor.RED + "You must be holding an item to set the arena icon!");
                    }
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "a":
                if (arena != null) {
                    Location location = player.getLocation();
                    arena.setA(CustomLocation.fromBukkitLocation(location));
                    player.sendMessage(ChatColor.GREEN + "Successfully set position A for arena " + args[1] + ".");
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "b":
                if (arena != null) {
                    Location location = player.getLocation();
                    arena.setB(CustomLocation.fromBukkitLocation(location));
                    player.sendMessage(ChatColor.GREEN + "Successfully set position B for arena " + args[1] + ".");
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "min":
                if (arena != null) {
                    arena.setMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                    player.sendMessage(ChatColor.GREEN + "Successfully set minimum position for arena " + args[1] + ".");
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "max":
                if (arena != null) {
                    arena.setMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                    player.sendMessage(ChatColor.GREEN + "Successfully set maximum position for arena " + args[1] + ".");
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "buildmax":
                if (arena != null) {
                    arena.setBuildMax(player.getLocation().getBlockY());
                    player.sendMessage(ChatColor.GREEN + "Successfully set maximum buildable height position for arena " + args[1] + ".");
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "deadzone":
                if (arena != null) {
                    arena.setDeadZone(player.getLocation().getBlockY());
                    player.sendMessage(CC.translate("&aSuccessfully set deadzone location for arena '&l" + arena.getName() + "&a'!"));
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "portalprot":
                if (arena != null) {
                    arena.setPortalProt(Integer.parseInt(args[2]));
                    player.sendMessage(ChatColor.GREEN + "Successfully set portal protection radius for arena " + args[1] + " to " + Integer.parseInt(args[2]) + ".");
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "disable":
            case "enable":
                if (arena != null) {
                    arena.setEnabled(!arena.isEnabled());
                    player.sendMessage(arena.isEnabled() ? ChatColor.GREEN + "Successfully enabled arena " + args[1] + "." :
                            ChatColor.RED + "Successfully disabled arena " + args[1] + ".");
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "info":
                if (arena != null) {
                    List<String> kitsWhitelisted = new ArrayList<>();

                    for (Kit kit : plugin.getKitManager().getKits()) {
                        if (kit.getArenaWhiteList().contains(arena.getName())) {
                            kitsWhitelisted.add(kit.getName());
                        }
                    }

                    player.sendMessage(CC.CHAT_BAR);
                    player.sendMessage(CC.translate("&b&lArena Information"));
                    player.sendMessage(" ");
                    player.sendMessage(CC.translate(" &9&l▸ &fName: &c" + arena.getName()));
                    player.sendMessage(CC.translate(" &9&l▸ &fState: " + (arena.isEnabled() ? "&aEnabled" : "&cDisabled")));
                    player.sendMessage(CC.translate(" &9&l▸ &fBuildMax: &c" + arena.getBuildMax()));
                    player.sendMessage(CC.translate(" &9&l▸ &fDeadZone: &c" + arena.getDeadZone()));
                    player.sendMessage(CC.translate(" &9&l▸ &fPortalProt Radius: &c" + arena.getPortalProt()));
                    player.sendMessage(CC.translate(" &9&l▸ &f1st Spawn: &c" + Math.round(arena.getA().getX()) + "&7, &c" + Math.round(arena.getA().getY()) + "&7, &c" + Math.round(arena.getA().getZ())));
                    player.sendMessage(CC.translate(" &9&l▸ &f2nd Spawn: &c" + Math.round(arena.getB().getX()) + "&7, &c" + Math.round(arena.getB().getY()) + "&7, &c" + Math.round(arena.getB().getZ())));
                    player.sendMessage(CC.translate(" &9&l▸ &fMin Location: &c" + Math.round(arena.getMin().getX()) + "&7, &c" + Math.round(arena.getMin().getY()) + "&7, &c" + Math.round(arena.getMin().getZ())));
                    player.sendMessage(CC.translate(" &9&l▸ &fMax Location: &c" + Math.round(arena.getMax().getX()) + "&7, &c" + Math.round(arena.getMax().getY()) + "&7, &c" + Math.round(arena.getMax().getZ())));
                    player.sendMessage(CC.translate(" &9&l▸ &fStandalone Arenas: &c" + arena.getStandaloneArenas().size()));
                    player.sendMessage(CC.translate(" &9&l▸ &fAvailable Arenas: &c" + (arena.getAvailableArenas().size() == 0 ? +1 : arena.getAvailableArenas().size())));
                    player.sendMessage(" ");
                    player.sendMessage(CC.translate(" &9&l▸ &fKits with this Arena: &c" + kitsWhitelisted.size()));
                    player.sendMessage(CC.translate("  &4 » &c" + StringUtils.join(kitsWhitelisted, "&f, &c")));
                    player.sendMessage(" ");
                    player.sendMessage(CC.CHAT_BAR);
                } else {
                    player.sendMessage(NO_ARENA);
                }
                break;
            case "generate":
                if (args.length == 3) {
                    int arenas = Integer.parseInt(args[2]);

                    this.plugin.getServer().getScheduler().runTask(this.plugin, new ArenaCommandRunnable(this.plugin, arena, arenas));
                    this.plugin.getArenaManager().setGeneratingArenaRunnable(this.plugin.getArenaManager().getGeneratingArenaRunnable() + 1);
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /arena generate <arena> <copies>");
                }
                break;
            default:
                player.sendMessage(CC.translate(command.getCommand().getUsage()));
                break;
        }
    }
}
