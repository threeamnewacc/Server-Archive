package cc.patrone.practice.commands.match;

import zone.potion.commands.PlayerCommand;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.inventory.menu.impl.DuelMenu;
import cc.patrone.practice.party.Party;
import cc.patrone.practice.player.MatchRequestHandler;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.entity.Player;

public class DuelCommand extends PlayerCommand {
	private final PracticePlugin plugin;

	public DuelCommand(PracticePlugin plugin) {
		super("duel");
		this.plugin = plugin;
		setUsage(CC.RED + "Usage: /duel <player>");
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
		MatchRequestHandler handler = targetProfile.getMatchRequestHandler();

		if (handler.getMatchRequest(player.getUniqueId()) != null) {
			player.sendMessage(CC.RED + "You already sent a match request to that player.\nPlease wait until it expires.");
			return;
		}

		if (targetProfile.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "That player isn't in spawn.");
			return;
		}

		if (!targetProfile.isAcceptingDuels()) {
			player.sendMessage(CC.RED + "That player isn't accepting duel requests.");
			return;
		}

		Party party = profile.getParty();

		if (party != null && !party.isProfileLeader(profile)) {
			player.sendMessage(CC.RED + "You aren't the leader!");
			return;
		}

		profile.setDuelSelecting(target.getUniqueId());
		plugin.getMenuManager().getMenu(DuelMenu.class).open(player);
	}
}
