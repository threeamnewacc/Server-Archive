package org.bukkit.command.defaults;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

@Deprecated
public class ExpCommand extends VanillaCommand {
	
    public ExpCommand() {
        super("xp");
        
        this.description = "Gives the specified player a certain amount of experience. Specify <amount>L to give levels instead, with a negative amount resulting in taking levels.";
        this.usageMessage = "/xp <amount> [player] OR /xp <amount>L [player]";
        
        this.setPermission("bukkit.command.xp");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length > 0) {
            String inputAmount = args[0];
            Player player = null;

            boolean isLevel = inputAmount.endsWith("l") || inputAmount.endsWith("L");
            if (isLevel && inputAmount.length() > 1) {
                inputAmount = inputAmount.substring(0, inputAmount.length() - 1);
            }

            int amount = getInteger(sender, inputAmount, Integer.MIN_VALUE, Integer.MAX_VALUE);
            boolean isTaking = amount < 0;

            if (isTaking) {
                amount *= -1;
            }

            if (args.length > 1) {
                player = Bukkit.getPlayer(args[1]);
            } else if (sender instanceof Player) {
                player = (Player) sender;
            }

            if (player != null) {
                if (isLevel) {
                    if (isTaking) {
                        player.giveExpLevels(-amount);
                        Command.broadcastCommandMessage(sender, "&aTaken &r" + amount + " &alevel(s) from &r" + player.getName());
                    } else {
                        player.giveExpLevels(amount);
                        Command.broadcastCommandMessage(sender, "&aGiven &r" + amount + " &alevel(s) to &r" + player.getName());
                    }
                } else {
                    if (isTaking) {
                        sender.sendMessage("&cTaking experience can only be done by levels, cannot give players negative experience points");
                        return false;
                    } else {
                        player.giveExp(amount);
                        Command.broadcastCommandMessage(sender, "&aGiven &r" + amount + "&a experience to &r" + player.getName());
                    }
                }
            } else {
                sender.sendMessage("&cCan't find player, was one provided?", "&cUsage: " + usageMessage);
                return false;
            }

            return true;
        }

        sender.sendMessage("&cUsage: " + usageMessage);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 2) {
            return super.tabComplete(sender, alias, args);
        }
        return ImmutableList.of();
    }
}
