package cc.patrone.practice.commands.time;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PlayerTimeType;

public class SunsetCommand extends TimeCommand {
	public SunsetCommand(PracticePlugin plugin) {
		super(PlayerTimeType.SUNSET, plugin);
	}
}
