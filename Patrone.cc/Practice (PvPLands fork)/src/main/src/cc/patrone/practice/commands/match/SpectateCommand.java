package cc.patrone.practice.commands.match;

import zone.potion.CorePlugin;
import zone.potion.commands.PlayerCommand;
import zone.potion.player.CoreProfile;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.match.Match;
import cc.patrone.practice.match.MatchState;
import cc.patrone.practice.match.MatchTeam;
import cc.patrone.practice.party.Party;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.entity.Player;

public class SpectateCommand extends PlayerCommand {
	private final PracticePlugin plugin;

	public SpectateCommand(PracticePlugin plugin) {
		super("spectate");
		this.plugin = plugin;
		setAliases("sp", "spec", "spect");
		setUsage(CC.RED + "Usage: /spectate <player>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1 || plugin.getServer().getPlayer(args[0]) == null) {
			player.sendMessage(usageMessage);
			return;
		}

		PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
		Party party = profile.getParty();

		if (party != null || profile.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return;
		}

		Player target = plugin.getServer().getPlayer(args[0]);
		PracticeProfile targetProfile = plugin.getPlayerManager().getProfile(target.getUniqueId());

		if (targetProfile.getPlayerState() != PlayerState.FIGHTING) {
			player.sendMessage(CC.RED + "Player is not in a match.");
			return;
		}

		boolean isNotStaff = !CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId()).hasStaff();

		if (!targetProfile.isAllowingSpectators() && isNotStaff) {
			player.sendMessage(CC.RED + "Player isn't allowing spectators");
			return;
		}

		Match targetMatch = targetProfile.getMatch();

		if (targetMatch.getMatchState() == MatchState.ENDED) {
			player.sendMessage(CC.RED + "That match just finished!");
			return;
		}

		for (MatchTeam team : targetMatch.getTeams()) {
			for (PracticeProfile practiceProfile : team.getPlayers()) {
				if (!practiceProfile.isAllowingSpectators() && isNotStaff) {
					player.sendMessage(CC.RED + "One of the players in this match isn't allowing spectators");
					return;
				}
			}
		}

		CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

		for (MatchTeam team : targetMatch.getTeams()) {
			for (Player teamPlayer : team.alivePlayers()) {
				if (!teamPlayer.isOnline()) {
					continue;
				}

				CoreProfile teamProfile = CorePlugin.getInstance().getProfileManager().getProfile(teamPlayer.getUniqueId());

				if (teamProfile == null) {
					continue;
				}

				if (!coreProfile.hasStaff() || (coreProfile.hasStaff() && teamProfile.hasStaff())) {
					teamPlayer.sendMessage(CC.AQUA + player.getName() + CC.PRIMARY + " is now spectating your match.");
				}
			}
		}

		plugin.getSpectatorManager().addSpectator(player, profile, target, targetMatch);
	}
}
