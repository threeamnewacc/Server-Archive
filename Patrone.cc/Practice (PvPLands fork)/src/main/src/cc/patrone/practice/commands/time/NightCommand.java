package cc.patrone.practice.commands.time;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PlayerTimeType;

public class NightCommand extends TimeCommand {
	public NightCommand(PracticePlugin plugin) {
		super(PlayerTimeType.NIGHT, plugin);
	}
}
