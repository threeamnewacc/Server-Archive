package us.zonix.core.symbols.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;
import us.zonix.core.api.request.PlayerRequest;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class PurchaseSymbolsCommand extends BaseCommand {

    @Command(name = "purchasesymbol", aliases = {"purchasesymbols"}, rank = Rank.MANAGER)
    public void onCommand(CommandArgs command) {
        CommandSender player = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /purchasesymbols [player] [true/false]");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        boolean value = Boolean.parseBoolean(args[1]);

        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PlayerRequest.UpdateBoughtSymbolsRequest(target.getUniqueId(), value));

        player.sendMessage(ChatColor.GREEN + "Updated " + target.getName() + " symbols to " + value + ".");

        if (value) {
            target.sendMessage(ChatColor.GREEN + "You now have access to all of the symbols.");
        }

        Profile.getByUuidIfAvailable(target.getUniqueId()).setBoughtSymbols(value);
    }

}
