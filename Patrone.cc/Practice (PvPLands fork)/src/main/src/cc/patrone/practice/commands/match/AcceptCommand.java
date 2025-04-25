package cc.patrone.practice.commands.match;

import zone.potion.commands.PlayerCommand;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.match.Match;
import cc.patrone.practice.match.MatchBuilder;
import cc.patrone.practice.match.MatchRequest;
import cc.patrone.practice.party.Party;
import cc.patrone.practice.player.MatchRequestHandler;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.entity.Player;

public class AcceptCommand extends PlayerCommand {
	private final PracticePlugin plugin;

	public AcceptCommand(PracticePlugin plugin) {
		super("accept");
		this.plugin = plugin;
		setUsage(CC.RED + "Usage: /accept <player>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1 || plugin.getServer().getPlayer(args[0]) == null) {
			player.sendMessage(usageMessage);
			return;
		}

		if (plugin.getArenaManager().getArenas().size() == 0) {
			player.sendMessage(CC.RED + "There are no arenas available!");
			return;
		}

		PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());

		if (profile.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return;
		}

		Player target = plugin.getServer().getPlayer(args[0]);

		if (player.getName().equals(target.getName())) {
			player.sendMessage(CC.RED + "You can't duel yourself.");
			return;
		}

		PracticeProfile targetProfile = plugin.getPlayerManager().getProfile(target.getUniqueId());

		if (targetProfile.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "Player is not in spawn.");
			return;
		}

		if ((profile.isInParty() && !targetProfile.isInParty()) || (!profile.isInParty() && targetProfile.isInParty())) {
			player.sendMessage(CC.RED + "Either you or the other person is not in a party.");
			return;
		}

		MatchRequestHandler handler = profile.getMatchRequestHandler();
		MatchRequest request = handler.getMatchRequest(target.getUniqueId());

		if (args.length > 1) {
			Kit kit = Kit.getByName(args[1]);

			if (kit != null) {
				if (plugin.getArenaManager().getArenas().size() == 0 || kit.getAvailableArenas().size() == 0) {
					player.sendMessage(CC.RED + "There are no arenas available!");
					return;
				}

				request = handler.getMatchRequest(target.getUniqueId(), kit.getName());
			}
		}

		if (request == null) {
			player.sendMessage(CC.RED + "You don't have a match request from that player.");
			return;
		}

		profile.getMatchRequestHandler().removeMatchRequest(request);

		if (request.getRequester().equals(target.getUniqueId())) {
			MatchBuilder builder = new MatchBuilder(plugin);

			if (request.isParty()) {
				Party party = profile.getParty();
				Party targetParty = targetProfile.getParty();

				if (party != null && targetParty != null && targetParty.isProfileLeader(targetProfile)) {
					builder.team(0, party.getMembers());
					builder.team(1, targetParty.getMembers());
					builder.party(true);
				} else {
					player.sendMessage(CC.RED + "Either you or that player is not a party leader.");
				}
			} else {
				builder.team(0, profile);
				builder.team(1, targetProfile);
			}

			Kit requestedKit = Kit.getByName(request.getKitName());

			if (requestedKit == null) {
				player.sendMessage(CC.RED + "That kit doesn't exist!");
				return;
			}

			if (requestedKit.getAvailableArenas().size() == 0) {
				player.sendMessage(CC.RED + "There are no arenas available for this kit!");
				return;
			}

			builder.arena(request.getArena());
			builder.kit(requestedKit);

			Match match = builder.build();

			match.broadcast(CC.PRIMARY + "Starting a match with kit "
					+ CC.SECONDARY + request.getKitName() + CC.PRIMARY + " between "
					+ match.getTeams().get(0).getLeader().getName() + " and " + match.getTeams().get(1).getLeader().getName() + ".");
			plugin.getMatchManager().startMatch(match);
		} else {
			player.sendMessage(CC.RED + "You don't have a match request from that player.");
		}
	}
}
