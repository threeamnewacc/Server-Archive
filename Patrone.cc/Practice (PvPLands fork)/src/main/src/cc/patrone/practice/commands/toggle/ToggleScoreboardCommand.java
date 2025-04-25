package cc.patrone.practice.commands.toggle;

import zone.potion.commands.PlayerCommand;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.entity.Player;

public class ToggleScoreboardCommand extends PlayerCommand {
	private final PracticePlugin plugin;

	public ToggleScoreboardCommand(PracticePlugin plugin) {
		super("togglescoreboard");
		this.plugin = plugin;
		setAliases("sb", "sidebar", "scoreboard", "togglesidebar", "tsb");
	}

	@Override
	public void execute(Player player, String[] args) {
		PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
		boolean scoreboardEnabled = !profile.isScoreboardEnabled();

		profile.setScoreboardEnabled(scoreboardEnabled);
		player.sendMessage(scoreboardEnabled ? CC.GREEN + "Toggled scoreboard on." : CC.RED + "Toggled scoreboard off.");
	}
}
