package cc.patrone.practice.inventory.menu.impl;

import zone.potion.inventory.menu.Menu;
import zone.potion.inventory.menu.action.Action;
import zone.potion.utils.item.ItemBuilder;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.arena.Arena;
import cc.patrone.practice.arena.ArenaType;
import cc.patrone.practice.party.Party;
import cc.patrone.practice.player.MatchRequestHandler;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SumoArenaMenu extends Menu {
	private final PracticePlugin plugin;

	public SumoArenaMenu(PracticePlugin plugin) {
		super(2, "Select an Arena to Duel On");
		this.plugin = plugin;
	}

	private Action getAction(Arena arena) {
		return player -> {
			PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
			Player selected = plugin.getServer().getPlayer(profile.getDuelSelecting());

			if (selected == null) {
				player.sendMessage(CC.RED + "Player is no longer online.");
				return;
			}

			PracticeProfile targetProfile = plugin.getPlayerManager().getProfile(selected.getUniqueId());

			if (targetProfile.getPlayerState() != PlayerState.SPAWN) {
				player.sendMessage(CC.RED + "Player is not in spawn.");
				return;
			}

			Party party = profile.getParty();
			Party targetParty = targetProfile.getParty();
			boolean partyDuel = party != null;

			if (partyDuel && targetParty == null) {
				player.sendMessage(CC.RED + "That player is not in a party.");
				return;
			}

			MatchRequestHandler profileHandler = profile.getMatchRequestHandler();

			if (profileHandler.getMatchRequest(selected.getUniqueId()) != null) {
				player.sendMessage(CC.RED + "You already sent a match request to that player. Please wait until it expires.");
				return;
			}

			MatchRequestHandler targetHandler = targetProfile.getMatchRequestHandler();
			Arena duelArena = arena == null ? plugin.getArenaManager().getRandomArena(ArenaType.SUMO, profile.getSelectedKit()) : arena;

			targetHandler.sendMatchRequest(player, selected, profile.getSelectedKit(), partyDuel, party, targetParty, duelArena);

			profile.setSelectedKit(null);
		};
	}

	@Override
	public void setup() {
		setActionableItem(0, new ItemBuilder(Material.PAPER).name(CC.ACCENT + "Random").build(), getAction(null));

		int count = 1;

		for (Arena arena : plugin.getArenaManager().getArenas(ArenaType.SUMO)) {
			setActionableItem(count++, new ItemBuilder(Material.EMPTY_MAP).name(CC.ACCENT + arena.getName()).build(), getAction(arena));
		}
	}

	@Override
	public void update() {
	}
}
