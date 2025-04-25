package cc.patrone.practice.inventory.menu.impl;

import zone.potion.inventory.menu.action.Action;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.inventory.menu.GenericKitMenu;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.match.Match;
import cc.patrone.practice.match.MatchBuilder;
import cc.patrone.practice.party.Party;
import cc.patrone.practice.player.PracticeProfile;
import com.google.common.collect.ImmutableList;

public class PartyFfaMenu extends GenericKitMenu {
	public PartyFfaMenu(PracticePlugin plugin) {
		super("Select a Kit to Fight With", plugin);
	}

	@Override
	protected Action getAction(Kit kit) {
		return player -> {
			if (plugin.getArenaManager().getArenas().size() == 0 || kit.getAvailableArenas().size() == 0) {
				player.sendMessage(CC.RED + "There are no arenas available!");
				return;
			}

			PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
			Party party = profile.getParty();

			if (party == null || !party.isProfileLeader(profile)) {
				return;
			}

			if (party.getMembers().size() < 2) {
				player.sendMessage(CC.RED + "You need more 2 or more players in your party to start an event.");
				return;
			}

			Match match = new MatchBuilder(plugin)
					.team(0, ImmutableList.copyOf(party.getMembers()))
					.party(true)
					.kit(kit)
					.build();

			match.broadcast(CC.PRIMARY + "Starting a party FFA match with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY + ".");
			plugin.getMatchManager().startMatch(match);
		};
	}

	@Override
	public void setup() {
	}
}
