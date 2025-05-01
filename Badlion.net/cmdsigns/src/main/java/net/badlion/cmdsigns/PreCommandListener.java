package net.badlion.cmdsigns;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PreCommandListener implements Listener {
	
	private CmdSigns plugin;
	
	public PreCommandListener(CmdSigns plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPreCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			if (player instanceof Player) {
				boolean hackCommand = false;
				String [] args = event.getMessage().split(" ");
				if (args.length >= 2) {
					if (this.plugin.getValidHashes().contains(args[1])) {
						hackCommand = true;
					}
				}
				
				// Replace <name>
				if (!event.getMessage().startsWith("/addsign")) {
					event.setMessage(event.getMessage().replace("<name>", player.getName()));
				}

				// Replace <uuid>
				if (!event.getMessage().startsWith("/addsign")) {
					event.setMessage(event.getMessage().replace("<uuid>", player.getUniqueId().toString()));
				}
				
				if (event.getMessage().startsWith("/give") || event.getMessage().startsWith("/enchant")) {	
					// Pass-thru with validation for hashcode
					if (args.length >= 2) {
						if (!player.isOp() && !this.plugin.getValidHashes().contains(args[1])) {
							player.sendMessage("Invalid authorization.");
							event.setCancelled(true);
							return;
						} else {
							StringBuilder string = new StringBuilder();
							args = event.getMessage().split(" ");
							for (int i = 0; i < args.length; ++i) {
								// Ignore hashcode
								if (i == 1)
									continue;
								string.append(args[i]);
								string.append(" ");
							}
							event.setMessage(string.toString());
						}
					}
				}
				
				// NOTE TO SELF
				// This is needed because we are not naturally calling the event with the CmdSignListner.java
				// So we don't actually execute the command...we have to force it to kick-start here.
				if (hackCommand) {
					// Get rid of extra slash
					player.performCommand(event.getMessage().substring(1, event.getMessage().length()));
				}
			}
		}
	}

}
