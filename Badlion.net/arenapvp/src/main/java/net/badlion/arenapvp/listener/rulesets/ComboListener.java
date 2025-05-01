package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.state.MatchState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.GCheatEvent;

public class ComboListener implements Listener {

	@EventHandler(priority = EventPriority.FIRST)
	public void onTypeC(GCheatEvent event) {
		if (MatchState.playerIsInMatchAndUsingRuleSet(event.getPlayer(), KitRuleSet.comboRuleSet)) {
			if (event.getType() == GCheatEvent.Type.KILL_AURA && event.getMsg().contains("Type C")) {
				int lvl = Integer.parseInt(event.getMsg().substring(event.getMsg().length() - 1));
				if (lvl == 2 || lvl == 3) {
					event.setCancelled(true);
				}
			}
		}
	}


}
