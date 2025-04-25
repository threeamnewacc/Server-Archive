package cc.patrone.practice.inventory.menu.impl;

import zone.potion.inventory.menu.action.Action;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.inventory.menu.GenericKitMenu;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.party.Party;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.entity.Player;

public class DuelMenu extends GenericKitMenu {
	public DuelMenu(PracticePlugin plugin) {
		super("Select a Kit to Duel With", plugin);
	}

	@Override
	protected Action getAction(Kit kit) {
		return player -> {
			if (plugin.getArenaManager().getArenas().size() == 0 || kit.getAvailableArenas().size() == 0) {
				player.sendMessage(CC.RED + "There are no arenas available!");
				return;
			}

			PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
			Player selected = plugin.getServer().getPlayer(profile.getDuelSelecting());

			if (selected == null) {
				player.sendMessage(CC.RED + "Player is no longer online.");
				return;
			}

			PracticeProfile targetData = plugin.getPlayerManager().getProfile(selected.getUniqueId());

			if (targetData.getPlayerState() != PlayerState.SPAWN) {
				player.sendMessage(CC.RED + "Player is not in spawn.");
				return;
			}

			Party party = profile.getParty();
			Party targetParty = targetData.getParty();
			boolean partyDuel = party != null;

			if (partyDuel && targetParty == null) {
				player.sendMessage(CC.RED + "That player is not in a party.");
				return;
			}

			player.closeInventory();

			if (kit == Kit.SUMO) {
				plugin.getMenuManager().getMenu(SumoArenaMenu.class).open(player);
			} else {
				plugin.getMenuManager().getMenu(ArenaMenu.class).open(player);
			}

			profile.setSelectedKit(kit);
		};
	}

	@Override
	public void setup() {
	}
}
