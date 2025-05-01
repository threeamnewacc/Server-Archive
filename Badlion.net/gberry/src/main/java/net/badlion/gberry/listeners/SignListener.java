package net.badlion.gberry.listeners;

import net.badlion.gberry.utils.FormatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		if (player.isOp()) {
			for (int i = 0; i < 4; i++) {
				event.setLine(i, FormatUtil.replaceColor(event.getLine(i), FormatUtil.REPLACE_COLOR_PATTERN));
			}
		} else {
			for (int i = 0; i < 4; i++) {
				event.setLine(i, FormatUtil.stripColor(event.getLine(i), FormatUtil.VANILLA_COLOR_PATTERN));
			}
		}
	}

}
