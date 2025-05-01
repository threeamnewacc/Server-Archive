package net.badlion.gfactions.managers;

import net.badlion.gfactions.Battle;
import net.badlion.gfactions.FightParticipant;
import net.badlion.gfactions.GFactions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BattleManager {

	public static Map<String, FightParticipant> playerToFightParticipantMap = new HashMap<>();
	private GFactions plugin;
	public static LinkedList<Battle> battles = new LinkedList<>();

	public BattleManager(GFactions plugin) {
		this.plugin = plugin;

		// Start the task up
		//new BattleEndCheckerTask(plugin).runTaskTimer(this.plugin, 60 * 20, 60 *20);
	}

	//public Battle mergeFights(Battle battle1, Battle battle2) {
	//
	//}


}
