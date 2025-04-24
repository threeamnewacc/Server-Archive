package club.minemen.practice.commands.duel;

import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.Practice;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.managers.PartyManager;
import club.minemen.practice.match.Match;
import club.minemen.practice.match.MatchRequest;
import club.minemen.practice.match.MatchTeam;
import club.minemen.practice.party.Party;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import club.minemen.practice.queue.QueueType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public AcceptCommand() {
		super("accept");
		this.setDescription("Accept a player's duel.");
		this.setUsage(CC.RED + "Usage: /accept <player>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;

		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return true;
		}
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return true;
		}
		Player target = this.plugin.getServer().getPlayer(args[0]);

		if (target == null) {
			player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
			return true;
		}
		if (player.getName().equals(target.getName())) {
			player.sendMessage(CC.RED + "You can't duel yourself.");
			return true;
		}
		PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());

		if (targetData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "Player is not in spawn.");
			return true;
		}
		MatchRequest request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId());

		if (args.length > 1) {
			Kit kit = this.plugin.getKitManager().getKit(args[1]);

			if (kit != null) {
				request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId(), kit.getName());
			}
		}
		if (request == null) {
			player.sendMessage(CC.RED + "You don't have a match request from that player.");
			return true;
		}
		if (request.getRequester().equals(target.getUniqueId())) {
			List<UUID> playersA = new ArrayList<>();
			List<UUID> playersB = new ArrayList<>();
			List<Integer> playerIdsA = new ArrayList<>();
			List<Integer> playerIdsB = new ArrayList<>();

			PartyManager partyManager = this.plugin.getPartyManager();

			Party party = partyManager.getParty(player.getUniqueId());
			Party targetParty = partyManager.getParty(target.getUniqueId());

			if (request.isParty()) {
				if (party != null && targetParty != null && partyManager.isLeader(target.getUniqueId()) &&
						partyManager.isLeader(target.getUniqueId())) {
					playersA.addAll(party.getMembers());
					playersB.addAll(targetParty.getMembers());
					party.getMembers().forEach(uuid -> playerIdsA.add(this.plugin.getPlayerManager().getPlayerData(uuid).getMinemanID()));
					targetParty.getMembers().forEach(uuid -> playerIdsB.add(this.plugin.getPlayerManager().getPlayerData(uuid).getMinemanID()));
				} else {
					player.sendMessage(CC.RED + "Either you or that player is not a party leader.");
					return true;
				}
			} else {
				if (party == null && targetParty == null) {
					playersA.add(player.getUniqueId());
					playersB.add(target.getUniqueId());
					playerIdsA.add(playerData.getMinemanID());
					playerIdsB.add(targetData.getMinemanID());
				} else {
					player.sendMessage(CC.RED + "One of you are in a party.");
					return true;
				}
			}

			Kit kit = this.plugin.getKitManager().getKit(request.getKitName());

			MatchTeam teamA = new MatchTeam(target.getUniqueId(), playersB, playerIdsA, 0);
			MatchTeam teamB = new MatchTeam(player.getUniqueId(), playersA, playerIdsB, 1);

			Match match = new Match(request.getArena(), kit, QueueType.UNRANKED, teamA, teamB);

			Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
			Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());

			match.broadcast(CC.PRIMARY + "Starting a match with kit " + CC.SECONDARY + request.getKitName() +
					CC.PRIMARY + " between " + CC.SECONDARY + leaderA.getName() + CC.PRIMARY + " and " + CC.SECONDARY + leaderB.getName() + CC.PRIMARY + ".");

			this.plugin.getMatchManager().createMatch(match);
		}

		return true;
	}
}
