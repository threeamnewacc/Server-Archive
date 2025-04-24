// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.duel;

import club.minemen.practice.party.Party;
import club.minemen.practice.managers.PartyManager;
import java.util.List;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.match.MatchRequest;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.match.Match;
import club.minemen.practice.queue.QueueType;
import club.minemen.practice.match.MatchTeam;
import java.util.Collection;
import java.util.UUID;
import java.util.ArrayList;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;

public class AcceptCommand extends Command
{
    private final Practice plugin;
    
    public AcceptCommand() {
        super("accept");
        this.plugin = Practice.getInstance();
        this.setDescription("Accept a player's duel.");
        this.setUsage(CC.RED + "Usage: /accept <player>");
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
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "You can't do this in your current state.");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        if (player.getName().equals(target.getName())) {
            player.sendMessage(CC.RED + "You can't duel yourself.");
            return true;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "Player is not in spawn.");
            return true;
        }
        MatchRequest request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId());
        if (args.length > 1) {
            final Kit kit = this.plugin.getKitManager().getKit(args[1]);
            if (kit != null) {
                request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId(), kit.getName());
            }
        }
        if (request == null) {
            player.sendMessage(CC.RED + "You don't have a match request from that player.");
            return true;
        }
        if (request.getRequester().equals(target.getUniqueId())) {
            final List<UUID> playersA = new ArrayList<UUID>();
            final List<UUID> playersB = new ArrayList<UUID>();
            final List<Integer> playerIdsA = new ArrayList<Integer>();
            final List<Integer> playerIdsB = new ArrayList<Integer>();
            final PartyManager partyManager = this.plugin.getPartyManager();
            final Party party = partyManager.getParty(player.getUniqueId());
            final Party targetParty = partyManager.getParty(target.getUniqueId());
            if (request.isParty()) {
                if (party == null || targetParty == null || !partyManager.isLeader(target.getUniqueId()) || !partyManager.isLeader(target.getUniqueId())) {
                    player.sendMessage(CC.RED + "Either you or that player is not a party leader.");
                    return true;
                }
                playersA.addAll(party.getMembers());
                playersB.addAll(targetParty.getMembers());
                party.getMembers().forEach(uuid -> playerIdsA.add(this.plugin.getPlayerManager().getPlayerData(uuid).getMinemanID()));
                targetParty.getMembers().forEach(uuid -> playerIdsB.add(this.plugin.getPlayerManager().getPlayerData(uuid).getMinemanID()));
            }
            else {
                if (party != null || targetParty != null) {
                    player.sendMessage(CC.RED + "One of you are in a party.");
                    return true;
                }
                playersA.add(player.getUniqueId());
                playersB.add(target.getUniqueId());
                playerIdsA.add(playerData.getMinemanID());
                playerIdsB.add(targetData.getMinemanID());
            }
            final Kit kit2 = this.plugin.getKitManager().getKit(request.getKitName());
            final MatchTeam teamA = new MatchTeam(target.getUniqueId(), playersB, playerIdsA, 0);
            final MatchTeam teamB = new MatchTeam(player.getUniqueId(), playersA, playerIdsB, 1);
            final Match match = new Match(request.getArena(), kit2, QueueType.UNRANKED, new MatchTeam[] { teamA, teamB });
            final Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
            final Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());
            match.broadcast(CC.PRIMARY + "Starting a match with kit " + CC.SECONDARY + request.getKitName() + CC.PRIMARY + " between " + CC.SECONDARY + leaderA.getName() + CC.PRIMARY + " and " + CC.SECONDARY + leaderB.getName() + CC.PRIMARY + ".");
            this.plugin.getMatchManager().createMatch(match);
        }
        return true;
    }
}
