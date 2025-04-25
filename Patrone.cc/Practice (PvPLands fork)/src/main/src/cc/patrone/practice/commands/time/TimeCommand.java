package cc.patrone.practice.commands.time;

import zone.potion.commands.PlayerCommand;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PlayerTimeType;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.entity.Player;

public class TimeCommand extends PlayerCommand {
	private final PlayerTimeType timeType;
	private final PracticePlugin plugin;

	TimeCommand(PlayerTimeType timeType, PracticePlugin plugin) {
		super(timeType.toString());
		this.timeType = timeType;
		this.plugin = plugin;
	}

	@Override
	public void execute(Player player, String[] strings) {
		PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());

		profile.setCurrentTimeType(timeType);

		timeType.apply(player);

		player.sendMessage(CC.PRIMARY + "Time set to " + CC.SECONDARY + timeType + CC.PRIMARY + ".");
	}
}
