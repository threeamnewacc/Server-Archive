package cc.patrone.practice.commands.time;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PlayerTimeType;

public class DayCommand extends TimeCommand {
	public DayCommand(PracticePlugin plugin) {
		super(PlayerTimeType.DAY, plugin);
	}
}
