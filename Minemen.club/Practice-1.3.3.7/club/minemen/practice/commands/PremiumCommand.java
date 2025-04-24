// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands;

import redis.clients.jedis.Jedis;
import club.minemen.core.util.cmd.annotation.commandTypes.SubCommand;
import club.minemen.practice.player.PlayerData;
import org.bukkit.entity.Player;
import club.minemen.core.api.callback.Callback;
import club.minemen.core.api.request.Request;
import org.bukkit.ChatColor;
import com.google.gson.JsonElement;
import club.minemen.core.api.abstr.AbstractBukkitCallback;
import club.minemen.core.CorePlugin;
import club.minemen.practice.request.PremiumRequest;
import club.minemen.practice.Practice;
import club.minemen.core.util.cmd.annotation.Flag;
import club.minemen.core.util.cmd.annotation.Param;
import club.minemen.core.rank.Rank;
import club.minemen.core.util.cmd.annotation.commandTypes.BaseCommand;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.cmd.CommandHandler;

public class PremiumCommand implements CommandHandler
{
    @BaseCommand(name = { "premium" }, rank = Rank.ADMIN)
    public void premiumCommand(final CommandSender sender) {
        sender.sendMessage(CC.RED + "Usage: /premium <add|take|set> [args...]");
    }
    
    @SubCommand(baseCommand = "premium", name = { "add" }, rank = Rank.ADMIN)
    public void addCommand(final CommandSender sender, @Param(name = "target") final String target, @Param(name = "amount") final int amount, @Flag(name = "d") final boolean donated) {
        if (amount == 0) {
            sender.sendMessage(CC.RED + "Amount must be > 0.");
            return;
        }
        if (target.trim().equals("")) {
            sender.sendMessage(CC.RED + "Invalid player.");
            return;
        }
        final Player player = Practice.getInstance().getServer().getPlayer(target);
        if (player != null) {
            final PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData != null) {
                playerData.setPremiumMatchesExtra(playerData.getPremiumMatchesExtra() + amount);
                sender.sendMessage(CC.GREEN + "Gave " + amount + " Premium Matches to " + player.getName());
                player.sendMessage(CC.PRIMARY + "You received " + CC.SECONDARY + amount + " Premium Matches" + CC.PRIMARY + (donated ? (CC.PRIMARY + ". Thanks for supporting Minemen Club") : (" from " + CC.SECONDARY + sender.getName())) + CC.PRIMARY + ".");
                return;
            }
        }
        final PremiumRequest request = new PremiumRequest("add", target, amount);
        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync((Request)request, (Callback)new AbstractBukkitCallback() {
            public void callback(final JsonElement jsonElement) {
                final String response = jsonElement.getAsJsonObject().get("response").getAsString();
                if (response.equals("success")) {
                    sender.sendMessage(CC.GREEN + "Gave " + amount + " Premium Matches to " + target);
                }
                else {
                    sender.sendMessage(ChatColor.RED + "There was an issue adding premium matches...");
                }
            }
        });
    }
    
    @SubCommand(baseCommand = "premium", name = { "take" }, rank = Rank.ADMIN)
    public void takeCommand(final CommandSender sender, @Param(name = "target") final String target, @Param(name = "amount") final int amount) {
        if (amount == 0) {
            sender.sendMessage(CC.RED + "Amount must be > 0.");
            return;
        }
        if (target.trim().equals("")) {
            sender.sendMessage(CC.RED + "Invalid player.");
            return;
        }
        final Player player = Practice.getInstance().getServer().getPlayer(target);
        if (player != null) {
            final PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData != null) {
                playerData.setPremiumMatchesExtra(playerData.getPremiumMatchesExtra() + amount);
                sender.sendMessage(CC.GREEN + "Removed " + amount + " Premium Matches from " + player.getName());
                return;
            }
        }
        final PremiumRequest request = new PremiumRequest("remove", target, amount);
        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync((Request)request, (Callback)new AbstractBukkitCallback() {
            public void callback(final JsonElement jsonElement) {
                final String response = jsonElement.getAsJsonObject().get("response").getAsString();
                if (response.equals("success")) {
                    sender.sendMessage(CC.GREEN + "Removed " + amount + " Premium Matches from " + target);
                }
                else {
                    sender.sendMessage(ChatColor.RED + "There was an issue removing premium matches...");
                }
            }
        });
    }
    
    @SubCommand(baseCommand = "premium", name = { "autism" }, rank = Rank.DEVELOPER)
    public void autism(final CommandSender sender) {
        CorePlugin.getInstance().runRedisCommand(jedis -> jedis.set("practice:premium:match_reset", System.currentTimeMillis() + ""));
        sender.sendMessage(ChatColor.YELLOW + "autism done");
        CorePlugin.getInstance().runRedisCommand(jedis -> sender.sendMessage(jedis.get("practice:premium:match_reset")));
    }
    
    @SubCommand(baseCommand = "premium", name = { "autism2" }, rank = Rank.DEVELOPER)
    public void autism2(final CommandSender sender) {
        CorePlugin.getInstance().runRedisCommand(jedis -> sender.sendMessage(jedis.get("practice:premium:match_reset")));
        sender.sendMessage(ChatColor.YELLOW + "autism2 done");
    }
}
