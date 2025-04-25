package cc.patrone.practice.match;

import zone.potion.CorePlugin;
import zone.potion.player.CoreProfile;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.arena.Arena;
import cc.patrone.practice.arena.ArenaType;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.player.PracticeProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MatchBuilder {
	private final PracticePlugin plugin;
	private final List<MatchTeam> teams = new ArrayList<>();
	private boolean party;
	private boolean ranked;
	private Kit kit;
	private Arena arena;

	public MatchBuilder(PracticePlugin plugin) {
		this.plugin = plugin;
	}

	public MatchBuilder party(boolean party) {
		this.party = party;
		return this;
	}

	public MatchBuilder ranked(boolean ranked) {
		this.ranked = ranked;
		return this;
	}

	public MatchBuilder kit(Kit kit) {
		this.kit = kit;
		return this;
	}

	public MatchBuilder arena(Arena arena) {
		this.arena = arena;
		return this;
	}

	public MatchBuilder team(int teamId, List<PracticeProfile> members) {
		List<PracticeProfile> temp = new ArrayList<>(members);
		teams.add(new MatchTeam(teamId, members.get(0), temp));
		return this;
	}

	public MatchBuilder team(int teamId, PracticeProfile... members) {
		List<PracticeProfile> temp = new ArrayList<>(Arrays.asList(members));
		teams.add(new MatchTeam(teamId, members[0], temp));
		return this;
	}

	public Match build() {
		this.arena = this.arena == null ? plugin.getArenaManager().getRandomArena(kit) : this.arena;

		if (kit == Kit.SUMO && arena.getArenaType() != ArenaType.SUMO) {
			arena = plugin.getArenaManager().getRandomArena(ArenaType.SUMO, kit);
		}

		if (arena == null) {
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(onlinePlayer.getUniqueId());

				if (onlinePlayer.isOp() || (profile != null && profile.hasStaff())) {
					onlinePlayer.sendMessage(CC.RED + "An error has occurred. Check console: (ERR: NO_ARENA_FOUND_FOR_KIT)");
					onlinePlayer.sendMessage(CC.RED + "Try adding an arena for this kit: " + kit.getName());
					onlinePlayer.sendMessage(CC.RED + "The server may need a reboot.");
				}
			}

			throw new IllegalStateException("No arenas found for kit " + kit.getName());
		}

		Match match = new Match(teams, kit, arena, party, ranked);

		for (MatchTeam matchTeam : match.getTeams()) {
			matchTeam.setMatch(match);
		}

		return match;
	}
}
