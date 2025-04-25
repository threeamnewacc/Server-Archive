package us.zonix.hcfactions.factions.commands.officer;

import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionWithdrawCommand extends FactionCommand {


    @Command(name = "f.withdraw", aliases = {"faction.withdraw", "factions.withdraw", "f.w", "faction.w", "factions.w"}, inFactionOnly = true, isOfficerOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(langConfig.getString("TOO_FEW_ARGS.WITHDRAW"));
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        PlayerFaction playerFaction = profile.getFaction();
        int amount;

        if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("a")) {
            amount = playerFaction.getBalance();
        } else {
            if (!(NumberUtils.isNumber(args[0]))) {
                player.sendMessage(langConfig.getString("ERROR.INVALID_NUMBER").replace("%STRING%", args[0]));
                return;
            }

            amount = (int) Math.floor(Double.valueOf(args[0]));

            if (amount > playerFaction.getBalance()) {
                player.sendMessage(langConfig.getString("ERROR.FACTION_NOT_ENOUGH_MONEY"));
                return;
            }
        }

        if (amount <= 0) {
            player.sendMessage(langConfig.getString("ERROR.INVALID_WITHDRAW_AMOUNT"));
            return;
        }

        profile.setBalance(profile.getBalance() + amount);

        playerFaction.setBalance(playerFaction.getBalance() - amount);
        playerFaction.sendMessage(langConfig.getString("ANNOUNCEMENTS.FACTION.PLAYER_WITHDRAW_MONEY").replace("%PLAYER%", player.getName()).replace("%AMOUNT%", amount + ""));
    }
}
