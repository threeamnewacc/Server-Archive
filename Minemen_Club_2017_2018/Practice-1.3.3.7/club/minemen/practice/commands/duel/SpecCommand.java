// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.duel;

import club.minemen.core.mineman.Mineman;
import club.minemen.practice.match.Match;
import club.minemen.practice.party.Party;
import club.minemen.practice.player.PlayerData;
import java.util.UUID;
import club.minemen.practice.match.MatchTeam;
import club.minemen.core.rank.Rank;
import club.minemen.core.CorePlugin;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Arrays;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;

public class SpecCommand extends Command
{
    private final Practice plugin;
    
    public SpecCommand() {
        super("spec");
        this.plugin = Practice.getInstance();
        this.setDescription("Spectate a player's match.");
        this.setUsage(CC.RED + "Usage: /spec <player>");
        this.setAliases((List)Arrays.asList("sp", "spect", "spectate"));
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (args.length < 1) {
            player.sendMessage(this.usageMessage);
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Party party = this.plugin.getPartyManager().getParty(playerData.getUniqueId());
        if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
            player.sendMessage(CC.RED + "You can't do this in your current state.");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.FIGHTING) {
            player.sendMessage(CC.RED + "Player is not in a match.");
            return true;
        }
        final Match targetMatch = this.plugin.getMatchManager().getMatch(targetData);
        final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        if (!targetMatch.isParty() && !mineman.hasRank(Rank.TRAINEE)) {
            if (!targetData.isAllowingSpectators()) {
                player.sendMessage(CC.RED + "This player is not allowing spectators.");
                return true;
            }
            final MatchTeam team = targetMatch.getTeams().get(0);
            final MatchTeam team2 = targetMatch.getTeams().get(1);
            final PlayerData otherPlayerData = this.plugin.getPlayerManager().getPlayerData((team.getPlayers().get(0) == target.getUniqueId()) ? team2.getPlayers().get(0) : team.getPlayers().get(0));
            if (otherPlayerData != null && !otherPlayerData.isAllowingSpectators()) {
                player.sendMessage(CC.RED + "The player this player is dueling is not allowing spectators.");
                return true;
            }
        }
        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            final Match match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());
            if (match.equals(targetMatch)) {
                player.sendMessage(CC.RED + "You are already spectating this match.");
                return true;
            }
            match.removeSpectator(player.getUniqueId());
        }
        player.sendMessage(CC.PRIMARY + "You are now spectating " + CC.SECONDARY + target.getName() + CC.PRIMARY + ".");
        this.plugin.getMatchManager().addSpectator(player, playerData, target, targetMatch);
        return true;
    }
}
