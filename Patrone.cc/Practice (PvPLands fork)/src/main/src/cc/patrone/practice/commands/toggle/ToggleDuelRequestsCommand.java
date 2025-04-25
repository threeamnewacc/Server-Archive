package cc.patrone.practice.commands.toggle;

import zone.potion.commands.PlayerCommand;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.entity.Player;

public class ToggleDuelRequestsCommand extends PlayerCommand {
	private final PracticePlugin plugin;

	public ToggleDuelRequestsCommand(PracticePlugin plugin) {
		super("toggleduelrequests");
		this.plugin = plugin;
		setAliases("tdr", "toggleduels", "toggleduel");
	}

	@Override
	public void execute(Player player, String[] args) {
		PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
		boolean acceptingDuels = !profile.isAcceptingDuels();

		profile.setAcceptingDuels(acceptingDuels);
		player.sendMessage(acceptingDuels ? CC.GREEN + "You are now accepting duel requests." : CC.RED + "You are no longer accepting duel requests.");
	}
}
