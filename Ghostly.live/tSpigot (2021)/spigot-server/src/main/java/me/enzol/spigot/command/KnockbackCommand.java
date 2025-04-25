package me.enzol.spigot.command;

import me.enzol.spigot.*;
import me.enzol.spigot.TrainingSpigot;
import me.enzol.spigot.knockback.KnockbackModule;
import me.enzol.spigot.knockback.KnockbackProfile;
import me.enzol.spigot.knockback.KnockbackValue;
import me.enzol.spigot.util.Util;
import org.bukkit.command.*;
import me.enzol.spigot.knockback.*;
import me.enzol.spigot.util.*;
import org.bukkit.entity.*;
import org.bukkit.craftbukkit.entity.*;
import org.bukkit.*;
import java.io.*;
import java.util.*;

public class KnockbackCommand extends Command {
    public KnockbackCommand() {
        super("knockback", "Knockback Command for Modify the settings", "/knockback " + TrainingSpigot.Aqua + " <arg>" + TrainingSpigot.Dark_Aqua + "\n  \u00bb " + TrainingSpigot.Gray + "list" + TrainingSpigot.Dark_Aqua + "\n  \u00bb " + TrainingSpigot.Gray + "view " + TrainingSpigot.Aqua + "<profile>" + TrainingSpigot.Dark_Aqua + "\n  \u00bb " + TrainingSpigot.Gray + "set/use " + TrainingSpigot.Aqua + "<profile> " + TrainingSpigot.Dark_Aqua + "<player>" + TrainingSpigot.Dark_Aqua + "\n  \u00bb " + TrainingSpigot.Gray + "create/add " + TrainingSpigot.Aqua + "<profile>" + TrainingSpigot.Dark_Aqua + "\n  \u00bb " + TrainingSpigot.Gray + "remove/delete " + TrainingSpigot.Aqua + "<profile>" + TrainingSpigot.Dark_Aqua + "\n  \u00bb " + TrainingSpigot.Gray + "edit " + TrainingSpigot.Aqua + "<profile> " + TrainingSpigot.Dark_Aqua + "<field> <value>", (List) Arrays.asList("kb"));
        super.setPermission("training.knockback");
        this.setPermission("TrainingSpigot.knockback");
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        if (args.length > 0) {
            Label_3131:
            {
                Label_1587:
                {
                    Label_1314:
                    {
                        final String lowerCase;
                        switch (lowerCase = args[0].toLowerCase()) {
                            case "create": {
                                break Label_1314;
                            }
                            case "delete": {
                                break Label_1587;
                            }
                            case "remove": {
                                break Label_1587;
                            }
                            case "add": {
                                break Label_1314;
                            }
                            case "set": {
                                break;
                            }
                            case "use": {
                                break;
                            }
                            case "edit": {
                                if (args.length < 2) {
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    sender.sendMessage(TrainingSpigot.Gray + "/knockback edit " + TrainingSpigot.Aqua + "<profile> " + TrainingSpigot.Dark_Aqua + "<field> <value>");
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    return true;
                                }
                                final KnockbackProfile profile = KnockbackModule.INSTANCE.profiles.get(args[1].toLowerCase());
                                if (profile == null) {
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    sender.sendMessage(TrainingSpigot.Gray + "Invalid profile: " + TrainingSpigot.Aqua + args[1].toLowerCase() + TrainingSpigot.Gray + ".");
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    return true;
                                }
                                final ArrayList<String> fields = new ArrayList<String>();
                                for (final KnockbackValue value : profile.values) {
                                    fields.add(value.id);
                                }
                                if (args.length < 3) {
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    sender.sendMessage(TrainingSpigot.Gray + "Available fields: " + TrainingSpigot.Aqua + Util.compile(fields.toArray(new String[0]), TrainingSpigot.Gray + ", " + TrainingSpigot.Aqua));
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    return true;
                                }
                                if (args.length == 4) {
                                    for (final KnockbackValue value : profile.values) {
                                        if (!value.id.equalsIgnoreCase(args[2])) {
                                            continue;
                                        }
                                        if (value.type == Integer.class) {
                                            try {
                                                value.value = Integer.valueOf(Integer.parseInt(args[3]));
                                                sender.sendMessage(TrainingSpigot.Separador);
                                                sender.sendMessage(TrainingSpigot.Gray + "Set profile " + TrainingSpigot.Aqua + profile.title + TrainingSpigot.Gray + " field " + TrainingSpigot.Aqua + value.name + TrainingSpigot.Gray + " to " + TrainingSpigot.Aqua + value.value + TrainingSpigot.Gray + ".");
                                                sender.sendMessage(TrainingSpigot.Separador);
                                                profile.save();
                                                return true;
                                            } catch (NumberFormatException ex) {
                                                sender.sendMessage(TrainingSpigot.Separador);
                                                sender.sendMessage(TrainingSpigot.Gray + "Invalid number.");
                                                sender.sendMessage(TrainingSpigot.Separador);
                                                return true;
                                            }
                                        }
                                        if (value.type == Double.class) {
                                            try {
                                                value.value = Double.valueOf(Double.parseDouble(args[3]));
                                                sender.sendMessage(TrainingSpigot.Separador);
                                                sender.sendMessage(TrainingSpigot.Gray + "Set profile " + TrainingSpigot.Aqua + profile.title + TrainingSpigot.Gray + " field " + TrainingSpigot.Aqua + value.name + TrainingSpigot.Gray + " to " + TrainingSpigot.Aqua + value.value + TrainingSpigot.Gray + ".");
                                                sender.sendMessage(TrainingSpigot.Separador);
                                                profile.save();
                                                return true;
                                            } catch (NumberFormatException ex2) {
                                                sender.sendMessage(TrainingSpigot.Separador);
                                                sender.sendMessage(TrainingSpigot.Gray + "Invalid number.");
                                                sender.sendMessage(TrainingSpigot.Separador);
                                                return true;
                                            }
                                        }
                                        if (value.type != Boolean.class) {
                                            return true;
                                        }
                                        try {
                                            value.value = Boolean.valueOf(Boolean.parseBoolean(args[3]));
                                            sender.sendMessage(TrainingSpigot.Separador);
                                            sender.sendMessage(TrainingSpigot.Gray + "Set profile " + TrainingSpigot.Aqua + profile.title + TrainingSpigot.Gray + " field " + TrainingSpigot.Aqua + value.name + TrainingSpigot.Gray + " to " + TrainingSpigot.Aqua + value.value + TrainingSpigot.Gray + ".");
                                            sender.sendMessage(TrainingSpigot.Separador);
                                            profile.save();
                                            return true;
                                        } catch (NumberFormatException ex3) {
                                            sender.sendMessage(TrainingSpigot.Separador);
                                            sender.sendMessage(TrainingSpigot.Gray + "Invalid number.");
                                            sender.sendMessage(TrainingSpigot.Separador);
                                            return true;
                                        }
                                    }
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    sender.sendMessage(TrainingSpigot.Gray + "Available fields: " + TrainingSpigot.Aqua + Util.compile(fields.toArray(new String[0]), TrainingSpigot.Gray + ", " + TrainingSpigot.Aqua));
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    return true;
                                }
                                sender.sendMessage(TrainingSpigot.Separador);
                                sender.sendMessage(TrainingSpigot.Gray + "/knockback edit " + TrainingSpigot.Aqua + "<profile> " + TrainingSpigot.Dark_Aqua + "<field> <value>");
                                sender.sendMessage(TrainingSpigot.Separador);
                                return true;
                            }
                            case "list": {
                                sender.sendMessage(TrainingSpigot.Separador);
                                sender.sendMessage(TrainingSpigot.Gray + "Knockback profiles: ");
                                sender.sendMessage(TrainingSpigot.Aqua + Util.compile(KnockbackModule.INSTANCE.profiles.keySet().toArray(new String[0]), TrainingSpigot.Gray));
                                sender.sendMessage(TrainingSpigot.Aqua);
                                sender.sendMessage(TrainingSpigot.Separador);
                                return true;
                            }
                            case "view": {
                                if (args.length != 2) {
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    sender.sendMessage(TrainingSpigot.Gray + "/knockback view " + TrainingSpigot.Aqua + "<profile>");
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    return true;
                                }
                                final KnockbackProfile profile = KnockbackModule.INSTANCE.profiles.get(args[1].toLowerCase());
                                if (profile != null) {
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    sender.sendMessage(TrainingSpigot.Gray + "Viewing profile " + TrainingSpigot.Aqua + profile.title + TrainingSpigot.Gray + ":");
                                    for (int i = 0; i < profile.values.size(); ++i) {
                                        final KnockbackValue value = profile.values.get(i);
                                        sender.sendMessage(TrainingSpigot.Dark_Aqua + ((i + 1 == profile.values.size()) ? "  \u00bb " : "  \u00bb ") + TrainingSpigot.Gray + value.name + ": " + TrainingSpigot.Aqua + value.value);
                                    }
                                    sender.sendMessage(TrainingSpigot.Separador);
                                    return true;
                                }
                                sender.sendMessage(TrainingSpigot.Separador);
                                sender.sendMessage(TrainingSpigot.Gray + "Invalid profile: " + TrainingSpigot.Aqua + args[1].toLowerCase());
                                sender.sendMessage(TrainingSpigot.Separador);
                                return true;
                            }
                            default:
                                break Label_3131;
                        }
                        if (args.length == 2) {
                            final KnockbackProfile profile = KnockbackModule.INSTANCE.profiles.get(args[1].toLowerCase());
                            if (profile != null) {
                                if (sender instanceof Player) {
                                    final Player player = (Player) sender;
                                    for (final Player other : player.getWorld().getPlayers()) {
                                        ((CraftPlayer) other).getHandle().setKnockback(profile);
                                    }
                                }
                                TrainingSpigot.INSTANCE.getConfig().set("knockback.default-profile", profile.title);
                                sender.sendMessage(TrainingSpigot.Separador);
                                sender.sendMessage(TrainingSpigot.Gray + "Updated knockback profile to " + TrainingSpigot.Aqua + profile.title);
                                sender.sendMessage(TrainingSpigot.Separador);
                                return true;
                            }
                            sender.sendMessage(TrainingSpigot.Separador);
                            sender.sendMessage(TrainingSpigot.Gray + "Invalid profile: " + TrainingSpigot.Aqua + args[1].toLowerCase());
                            sender.sendMessage(TrainingSpigot.Separador);
                            return true;
                        } else {
                            if (args.length != 3) {
                                sender.sendMessage(TrainingSpigot.Separador);
                                sender.sendMessage(TrainingSpigot.Gray + "/knockback " + TrainingSpigot.Aqua + "set" + TrainingSpigot.Gray + "/" + TrainingSpigot.Aqua + "use " + TrainingSpigot.Dark_Aqua + "<profile> <player>");
                                sender.sendMessage(TrainingSpigot.Separador);
                                return true;
                            }
                            final KnockbackProfile profile = KnockbackModule.INSTANCE.profiles.get(args[1].toLowerCase());
                            if (profile != null) {
                                if (sender instanceof Player) {
                                    final Player playerTarget = Bukkit.getPlayer(args[2]);
                                    if (!Bukkit.getOnlinePlayers().contains(playerTarget)) {
                                        sender.sendMessage(TrainingSpigot.Separador);
                                        sender.sendMessage(TrainingSpigot.Gray + "The player called \"" + TrainingSpigot.Aqua + args[2] + TrainingSpigot.Gray + "\" is offline.");
                                        sender.sendMessage(TrainingSpigot.Separador);
                                        return true;
                                    }
                                    ((CraftPlayer) playerTarget).getHandle().setKnockback(profile);
                                }
                                sender.sendMessage(TrainingSpigot.Separador);
                                sender.sendMessage(TrainingSpigot.Gray + "Updated knockback profile of player " + TrainingSpigot.Aqua + args[2] + TrainingSpigot.Gray + " to " + TrainingSpigot.Dark_Aqua + profile.title);
                                sender.sendMessage(TrainingSpigot.Separador);
                                return true;
                            }
                            sender.sendMessage(TrainingSpigot.Separador);
                            sender.sendMessage(TrainingSpigot.Gray + "Invalid profile: " + TrainingSpigot.Aqua + args[1].toLowerCase());
                            sender.sendMessage(TrainingSpigot.Separador);
                            return true;
                        }
                    }
                    if (args.length != 2) {
                        sender.sendMessage(TrainingSpigot.Separador);
                        sender.sendMessage(TrainingSpigot.Gray + "/knockback " + TrainingSpigot.Aqua + "create" + TrainingSpigot.Gray + "/" + TrainingSpigot.Aqua + "add " + TrainingSpigot.Dark_Aqua + "<profile>");
                        sender.sendMessage(TrainingSpigot.Separador);
                        return true;
                    }
                    if (!KnockbackModule.INSTANCE.profiles.containsKey(args[1].toLowerCase())) {
                        KnockbackModule.INSTANCE.profiles.put(args[1].toLowerCase(), new KnockbackProfile(args[1].toLowerCase()));
                        sender.sendMessage(TrainingSpigot.Separador);
                        sender.sendMessage(TrainingSpigot.Gray + "Knockback profile created " + TrainingSpigot.Aqua + args[1].toLowerCase() + TrainingSpigot.Gray + ".");
                        sender.sendMessage(TrainingSpigot.Separador);
                        return true;
                    }
                    sender.sendMessage(TrainingSpigot.Separador);
                    sender.sendMessage(TrainingSpigot.Gray + "Knockback profile already exists.");
                    sender.sendMessage(TrainingSpigot.Separador);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(TrainingSpigot.Separador);
                    sender.sendMessage(TrainingSpigot.Gray + "/knockback " + TrainingSpigot.Aqua + "remove" + TrainingSpigot.Gray + "/" + TrainingSpigot.Aqua + "delete " + TrainingSpigot.Dark_Aqua + "<profile>");
                    sender.sendMessage(TrainingSpigot.Separador);
                    return true;
                }
                if (args[1].equalsIgnoreCase("default")) {
                    sender.sendMessage(TrainingSpigot.Separador);
                    sender.sendMessage(TrainingSpigot.Gray + "Cannot delete the default profile.");
                    sender.sendMessage(TrainingSpigot.Separador);
                    return true;
                }
                if (KnockbackModule.INSTANCE.profiles.containsKey(args[1].toLowerCase())) {
                    new File("Knockback" + File.separator + args[1].toLowerCase() + ".yml").delete();
                    KnockbackModule.INSTANCE.profiles.remove(args[1].toLowerCase());
                    sender.sendMessage(TrainingSpigot.Separador);
                    sender.sendMessage(TrainingSpigot.Gray + "Knockback profile deleted " + TrainingSpigot.Aqua + args[1].toLowerCase() + TrainingSpigot.Gray + ".");
                    sender.sendMessage(TrainingSpigot.Separador);
                    return true;
                }
                sender.sendMessage(TrainingSpigot.Separador);
                sender.sendMessage(TrainingSpigot.Gray + "Knockback profile doesn't exist.");
                sender.sendMessage(TrainingSpigot.Separador);
                return true;
            }
            sender.sendMessage(TrainingSpigot.Separador);
            sender.sendMessage(TrainingSpigot.Gray + this.usageMessage);
            sender.sendMessage(TrainingSpigot.Separador);
            return true;
        }
        sender.sendMessage(TrainingSpigot.Separador);
        sender.sendMessage(TrainingSpigot.Gray + this.usageMessage);
        sender.sendMessage(TrainingSpigot.Separador);
        return true;

    }

}
