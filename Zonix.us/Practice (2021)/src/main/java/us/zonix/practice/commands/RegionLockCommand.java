package us.zonix.practice.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.maiko.dexter.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;

public class RegionLockCommand extends Command {
    private static final List<String> ALL_REGIONS = Arrays.asList("EU", "SA", "NA", "AS", "OC", "AF", "AN");

    public RegionLockCommand() {
        super("regionlock", "Zonix's Region Lock command..", "/regionlock", Arrays.asList("continentlock", "region"));
    }

    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if (commandSender instanceof Player && !commandSender.isOp()) {
            return true;
        } else if (args.length != 2) {
            return this.sendHelp(commandSender);
        } else {
            String var4 = args[0].toLowerCase();
            String continent = args[1].toUpperCase();
            List<String> allowedContinents = new ArrayList<>(Practice.getInstance().getAllowedRegions());
            switch (var4) {
                case "add":
                    if (ALL_REGIONS.stream().noneMatch(region -> region.equalsIgnoreCase(continent))) {
                        commandSender.sendMessage(
                            new String[]{
                                ChatColor.RED + "That continent is not a valid continent.",
                                ChatColor.YELLOW + "Valid continents: " + ChatColor.RED + String.join(", ", ALL_REGIONS)
                            }
                        );
                        return true;
                    } else {
                        if (allowedContinents.contains(continent)) {
                            commandSender.sendMessage(ChatColor.RED + "That continent is already on the continent list.");
                            return true;
                        }

                        allowedContinents.add(continent);
                        Practice.getInstance().setAllowedRegions(allowedContinents);
                        commandSender.sendMessage(ChatColor.GREEN + String.format("Successfully added %s to the continent list.", continent));
                        return true;
                    }
                case "remove":

                    if (!allowedContinents.contains(continent)) {
                        commandSender.sendMessage(ChatColor.RED + "That continent is not on the continent list.");
                        return true;
                    }

                    allowedContinents.remove(continent);
                    Practice.getInstance().setAllowedRegions(allowedContinents);
                    commandSender.sendMessage(ChatColor.GREEN + String.format("Successfully removed %s from the continent list.", continent));
                    return true;
                case "list":
                    if (!args[1].equalsIgnoreCase("allowed") && !args[1].equalsIgnoreCase("banned")) {
                        return this.sendHelp(commandSender);
                    } else {
                        boolean allowedList = args[1].equals("allowed");
                        if (allowedList) {
                            commandSender.sendMessage(ChatColor.YELLOW + "Allowed Continents: " + ChatColor.RED + String.join(", ", allowedContinents));
                            return true;
                        }

                        commandSender.sendMessage(
                            ChatColor.YELLOW
                                + "Disallowed Continents: "
                                + ChatColor.RED
                                + ALL_REGIONS.stream().filter(s -> !allowedContinents.contains(s)).collect(Collectors.joining(", "))
                        );
                        return true;
                    }
                case "toggle":
                    if (!args[1].equalsIgnoreCase("on") && !args[1].equalsIgnoreCase("off")) {
                        return true;
                    } else {
                        boolean on = args[1].equalsIgnoreCase("on");
                        if (on) {
                            Practice.getInstance().setRegionLock(true);
                            commandSender.sendMessage(ChatColor.GREEN + "Successfully toggled region-lock on.");
                            return true;
                        }

                        Practice.getInstance().setRegionLock(false);
                        commandSender.sendMessage(ChatColor.GREEN + "Successfully toggled region-lock off.");
                        return true;
                    }
                case "check":
                    Optional<Player> player = this.parsePlayer(args[1]);
                    if (!player.isPresent()) {
                        return this.playerNotFound(commandSender, args[1]);
                    }

                    Profile profile = Profile.getByUuid(player.get().getUniqueId());
                    commandSender.sendMessage(
                        profile.hasVpnData()
                            ? ChatColor.GREEN + String.format("%s's continent is: %s", player.get().getName(), profile.getVpnData().getContinent())
                            : ChatColor.RED + "That player's region is not defined."
                    );
                    return true;
                default:
                    return this.sendHelp(commandSender);
            }
        }
    }

    private boolean sendHelp(CommandSender sender) {
        sender.sendMessage(
            new String[]{
                ChatColor.RED + "Zonix Region Lock",
                ChatColor.GRAY + "/regionlock add <region> - Add a region.",
                ChatColor.GRAY + "/regionlock remove <region> - Remove a region.",
                ChatColor.GRAY + "/regionlock toggle <on:off> - Toggle regionlock.",
                ChatColor.GRAY + "/regionlock list <allowed:banned>",
                ChatColor.GRAY + "/regionlock check <player> - Check a player's continent"
            }
        );
        return true;
    }

    private boolean playerNotFound(CommandSender sender, String name) {
        sender.sendMessage(ChatColor.RED + String.format("The player with name %s could not be found.", name));
        return true;
    }

    private Optional<Player> parsePlayer(String name) {
        return Optional.ofNullable(Bukkit.getPlayer(name));
    }
}
